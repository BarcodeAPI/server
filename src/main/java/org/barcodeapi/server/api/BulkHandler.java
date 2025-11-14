package org.barcodeapi.server.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
import com.opencsv.exceptions.CsvValidationException;

/**
 * BulkHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BulkHandler extends RestHandler {

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

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws ServletException, IOException {

		try {

			// Get the uploaded file
			Part part = c.getRequest().getPart("csvFile");

			// Parse the CSV for barcode request objects
			List<BarcodeRequest> requests = parseRequests(part.getInputStream());

			if (requests == null) {

				// Log and return the processing failure
				LibLog._log("Failed to generate bulk barcodes.");
				throw new GenerationException(ExceptionType.INVALID, //
						new IllegalArgumentException("Invalid CSV format."));
			}

			// Determine max batch size for user
			Subscriber sub = c.getSubscriber();
			int maxBatch = (sub != null) ? sub.getMaxBatch() : 250;

			// Check if larger then allowed
			if (requests.size() > maxBatch) {

				// Return invalid request to user
				throw new GenerationException(ExceptionType.INVALID, //
						new Throwable("Request is larger then max batch size."));
			}

			// Determine token cost
			int tokenCount = 0;
			for (BarcodeRequest request : requests) {
				tokenCount += request.getCost();
			}

			// Discount for running as a batch
			int batchCost = ((tokenCount * 75) / 100);

			// Try to spend the tokens
			if (!c.getLimiter().spendTokens(batchCost)) {

				// Return rate limited barcode to user
				throw new GenerationException(ExceptionType.LIMITED, //
						new Throwable("Client is rate limited, try again later."));
			}

			// Response headers for file download
			r.setContentType("application/zip");
			r.setHeader("Content-Disposition", "filename=barcodes.zip");

			// Advise current token spend and count
			r.setHeader("X-RateLimit-Cost", Double.toString(batchCost));
			r.setHeader("X-RateLimit-Tokens", c.getLimiter().getTokenCountStr());

			// Pass request list and output stream to bulk helper
			getZippedBarcodes(requests, r.getOutputStream());

		} catch (GenerationException e) {

			// Log the failure
			LibLog._clogF("E0509", e.getMessage());

			// Print error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "failed to process bulk request")//
					.put("error", e.getMessage())//
			).toString());
		} catch (Exception | Error e) {

			// Print unknown failure to client
			r.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 500)//
					.put("message", "unknown error")//
					.put("error", e.getMessage())//
			).toString());
		}
	}

	private List<BarcodeRequest> parseRequests(InputStream in) {
		LibMetrics.hitMethodRunCounter();

		// Create CSV Reader from the input stream
		List<BarcodeRequest> requests = new ArrayList<>();
		try (CSVReader csvReader = new CSVReader(new InputStreamReader(in))) {

			String[] csvRecord;

			// Loop each entry in the CSV
			while ((csvRecord = csvReader.readNext()) != null) {
				try {

					requests.add(BarcodeRequest.fromCSV(csvRecord));

				} catch (GenerationException e) {

					LibLog._log("Failed to create barcode request.", e);
				}
			}

			return requests;
		} catch (IOException e) {

			// Log failure in input stream
			LibLog._log("Failed to load CSV.", e);
			return null;

		} catch (CsvValidationException e) {

			// Log failure in CSV validation
			LibLog._log("Failed to validate CSV.", e);
			return null;
		}
	}

	public static void getZippedBarcodes(List<BarcodeRequest> requests, OutputStream out) {
		LibMetrics.hitMethodRunCounter();

		// Open a new ZIP file output stream
		try (ZipOutputStream zipArchive = new ZipOutputStream(out)) {

			// Loop each of the requests
			for (BarcodeRequest request : requests) {

				// Generate the barcode
				CachedBarcode barcode;

				try {

					// Generate the requested barcode
					barcode = BarcodeGenerator.requestBarcode(request);
				} catch (GenerationException e) {

					// Log failure generating barcode
					LibLog._log("Failed to generate barcode.", e);
					continue;
				}

				try {

					// Add barcode entry to ZIP archive
					zipArchive.putNextEntry(new ZipEntry(barcode.getBarcodeStringNice() + ".png"));
					zipArchive.write(barcode.getBarcodeData(), 0, barcode.getBarcodeDataSize());
					zipArchive.closeEntry();

				} catch (Exception | Error e) {

					// Log failure adding to archive
					LibLog._log("Failed adding barcode to ZIP archive.", e);
				}
			}

			// Close the ZIP file
			zipArchive.close();

			// Close the stream
			out.flush();
			out.close();
		} catch (IOException e) {

			// Log general failure with ZIP archive
			LibLog._log("Failed creating ZIP archive.");
		}
	}
}
