package org.barcodeapi.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * BulkUtils.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BulkUtils {

	public static void getZippedBarcodes(InputStream in, OutputStream out)//
			throws IOException, GenerationException {
		LibMetrics.hitMethodRunCounter();

		ZipOutputStream zipArchive = new ZipOutputStream(out);

		try (CSVReader csvReader = new CSVReader(new InputStreamReader(in))) {

			String[] record;
			CachedBarcode barcode;

			// Loop each entry in the CSV
			while ((record = csvReader.readNext()) != null) {
				try {

					// Generate the barcode
					barcode = BarcodeGenerator//
							.requestBarcode(BarcodeRequest.fromCSV(record));

					// Add barcode entry to ZIP archive
					zipArchive.putNextEntry(new ZipEntry(barcode.getBarcodeStringNice() + ".png"));
					zipArchive.write(barcode.getBarcodeData(), 0, barcode.getBarcodeDataSize());
					zipArchive.closeEntry();

				} catch (GenerationException e) {
					LibLog._log("Failed to create barcode.", e);
				}
			}

			// Close the stream
			zipArchive.close();
			out.close();
		} catch (CsvValidationException e) {

			// Log and return the processing failure
			LibLog._log("Failed to generate bulk barcodes.");
			throw new GenerationException(ExceptionType.INVALID, e);
		}
	}
}
