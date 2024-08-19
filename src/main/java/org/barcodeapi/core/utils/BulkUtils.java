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

	public static void getZippedBarcodes(int max, InputStream in, OutputStream out)//
			throws IOException, GenerationException {
		LibMetrics.hitMethodRunCounter();

		ZipOutputStream zipArchive = new ZipOutputStream(out);

		try (CSVReader csvReader = new CSVReader(new InputStreamReader(in))) {

			String[] record;
			CachedBarcode barcode;
			ZipEntry zipEntry;

			// Loop each entry in the CSV
			while ((record = csvReader.readNext()) != null) {
				try {

					barcode = BarcodeGenerator//
							.requestBarcode(BarcodeRequest.fromCSV(record));

					zipEntry = new ZipEntry(barcode.getBarcodeStringNice() + ".png");

					zipArchive.putNextEntry(zipEntry);
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

			LibLog._log("Failed to generate bulk barcode.");
			throw new GenerationException(ExceptionType.INVALID, e);
		}

	}
}
