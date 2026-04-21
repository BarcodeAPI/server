package org.barcodeapi.server.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.cache.Subscriber;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

/**
 * BulkHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BulkHandler extends RestHandler {

	// Upload size in bytes
	private static final int UPLOAD_BYTES_MIN = 8;
	private static final int UPLOAD_BYTES_MAX = (512 * 1024);

	private final File uploadDir;

	public BulkHandler() {
		super(
				// Authentication not required
				false,
				// Use client rate limit
				true,
				// Do not create new session
				false);

		this.enableMultipart(true);

		this.uploadDir = new File("uploads", "bulk");
		this.uploadDir.mkdirs();
	}

	private String[][] readInput(InputStream stream) {
		LibMetrics.hitMethodRunCounter();

		// Create CSV Reader from the input stream
		List<String[]> records = new ArrayList<>();
		try (CSVReader csvReader = new CSVReader(new InputStreamReader(stream))) {
			String[] record;

			// Loop each entry in the CSV
			while ((record = csvReader.readNext()) != null) {

				records.add(record);
			}

			// Return the records as an array
			return records.toArray(//
					new String[records.size()][]);
		} catch (IOException e) {

			// Log failure in input stream
			LibLog._log("Failed to read CSV data.", e);
			return null;

		} catch (CsvValidationException e) {

			// Log failure in CSV validation
			LibLog._log("Failed to validate CSV format.", e);
			return null;
		}
	};

	private boolean writeFile(String uid, String[][] entries) {
		LibMetrics.hitMethodRunCounter();

		File outputFile = new File(uploadDir, uid + ".csv");

		try (CSVWriter csvWriter = //
				new CSVWriter(//
						new FileWriter(outputFile, true), //
						CSVWriter.DEFAULT_SEPARATOR, //
						CSVWriter.DEFAULT_QUOTE_CHARACTER, //
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, //
						CSVWriter.DEFAULT_LINE_END)) {

			for (String[] entry : entries) {
				csvWriter.writeNext(entry, false);
			}
			return true;

		} catch (IOException e) {
			LibLog._log("Failed to write file to disk.", e);
			return false;
		}
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws ServletException, IOException {

		// Assign the request an ID
		String uid = UUID.randomUUID().toString();

		double batchCost = 0;

		try {

			// Check for image
			if (!c.hasBody() || //
					c.getBodySize() < UPLOAD_BYTES_MIN || //
					c.getBodySize() > UPLOAD_BYTES_MAX) {

				// Send error to client
				throw new GenerationException(ExceptionType.INVALID, //
						new IllegalArgumentException("Body outside accepted size."));
			}

			// Get the uploaded file
			Part part = c.getRequest().getPart("csvFile");

			// Read the CSV file from user stream
			String[][] entries = readInput(part.getInputStream());

			// Write the file to disk
			writeFile(uid, entries);

			// Check entries loaded
			if (entries == null) {

				// Log and return the processing failure
				throw new GenerationException(ExceptionType.INVALID, //
						new Throwable("Bulk upload file not in CSV format."));
			}

			// Determine max batch size for user
			Subscriber sub = c.getSubscriber();
			int maxBatch = (sub != null) ? sub.getMaxBatch() : 250;

			// Build debug status message
			StringBuilder statusMessage = new StringBuilder("BarcodeAPI.org Bulk Generator Log\n");
			statusMessage.append(String.format(" Request Size: %d / %d\n", entries.length, maxBatch));

			// Open a new ZIP file output stream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zipArchive = new ZipOutputStream(baos);

			CachedBarcode barcode;
			int entriesComplete = 0;

			// Loop each of the requests
			int numEntries = (maxBatch > entries.length) ? entries.length : maxBatch;
			for (int index = 0; index < numEntries; index++) {
				String[] entry = entries[index];

				// Log the barcode data line
				statusMessage.append(String.format(//
						"\nProcessing entry: %d\n", (index + 1)));

				try {

					// Parse request from CSV entry
					BarcodeRequest request = BarcodeRequest.fromCSV(entry);

					// Log the data, type, and cost
					statusMessage.append(String.format(//
							" Data: %s\n Type: %s\n Cost: %.2f\n", //
							request.getData(), request.getType().getName(), request.getCost()));

					// Spend the tokens for the barcode
					if (!c.getLimiter().userRequest(true, request.getCost())) {
						statusMessage.append("\nClient is out of tokens!");
						break;
					}
					batchCost += request.getCost();

					// Generate the requested barcode
					statusMessage.append(" Generating...\n");
					barcode = BarcodeGenerator.requestBarcode(request);
				} catch (GenerationException e) {

					// Log failure generating barcode
					LibLog._clogF("E6009", entry[0], e.getCause().getMessage());
					statusMessage.append(String.format(" Failed: %s\n", e.getCause().getMessage()));
					continue;
				}

				try {

					// Add barcode entry to ZIP archive
					statusMessage.append(" Adding file to archive...\n");
					zipArchive.putNextEntry(new ZipEntry(barcode.getBarcodeStringNice() + ".png"));
					zipArchive.write(barcode.getBarcodeData(), 0, barcode.getBarcodeDataSize());
					zipArchive.closeEntry();
					statusMessage.append(" Done.\n");
					entriesComplete++;

				} catch (Exception | Error e) {

					// Log failure adding to archive
					LibLog._log("Failed adding barcode to ZIP archive!", e);
					statusMessage.append(String.format(" Failed: %s\n", e.getMessage()));
				}
			}

			// Log the bulk process
			long processTime = (System.currentTimeMillis() - c.getTimestamp());
			LibLog._clogF("I0602", entries.length, processTime, batchCost);

			// Add final status messages
			statusMessage.append("\n\nBulk processing complete.\n");
			statusMessage.append(String.format(" Count: %d / %d\n", entriesComplete, numEntries));
			statusMessage.append(String.format(" Cost:  %.2f tokens\n", batchCost));
			statusMessage.append(String.format(" Time:  %dms\n", processTime));
			statusMessage.append(String.format("\nTokens Remaining: %.2f\n", c.getLimiter().getTokens().getCount()));
			statusMessage.append(String.format("\nRequest: %s\n", uid));

			// Add debug messages to ZIP file
			byte[] debugBytes = statusMessage.toString().getBytes();
			zipArchive.putNextEntry(new ZipEntry("_debug.txt"));
			zipArchive.write(debugBytes, 0, debugBytes.length);

			// Close the ZIP file
			zipArchive.close();
			baos.flush();

			// Advise current token spend and count
			r.setHeader("X-RateLimit-Cost", Double.toString(batchCost));
			r.setHeader("X-RateLimit-Tokens", c.getLimiter().getTokens().getCountStr());

			// Response headers for file download
			r.setContentType("application/zip");
			r.setHeader("Content-Disposition", "filename=barcodes.zip");

			OutputStream out = r.getOutputStream();
			out.write(baos.toByteArray());
			out.flush();

		} catch (GenerationException e) {

			// Log the failure
			LibLog._clogF("E0509", e.getMessage());

			// Print error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("X-Error-Message", e.getCause().getMessage());
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("request", uid)//
					.put("message", "Failed to process bulk request.")//
					.put("error", e.getCause().getMessage())//
			).toString());
		} catch (Exception | Error e) {

			// Log the failure
			LibLog._logF("Unknown error: %s", e.getMessage());

			// Print unknown failure to client
			r.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			r.setHeader("X-Error-Message", e.getMessage());
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 500)//
					.put("request", uid)//
					.put("message", "Unknown error.")//
					.put("error", e.getMessage())//
			).toString());
		}
	}

}
