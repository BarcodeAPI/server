package org.barcodeapi.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
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

		ArrayList<BarcodeRequest> requests = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {

			String[] record;
			while ((record = reader.readNext()) != null) {
				if (requests.size() < max) {
					requests.add(buildBarcodeRequest(record));
				} else {
					break;
				}
			}
			reader.close();
		} catch (CsvValidationException e) {

			throw new GenerationException(ExceptionType.INVALID, e);
		}

		ArrayList<CachedBarcode> barcodes = generateBarcodes(requests);

		ZipOutputStream zip = new ZipOutputStream(out);

		for (CachedBarcode barcode : barcodes) {

			ZipEntry zipEntry = new ZipEntry(barcode.getBarcodeStringNice() + ".png");
			zip.putNextEntry(zipEntry);
			zip.write(barcode.getBarcodeData(), 0, barcode.getBarcodeDataSize());
			zip.closeEntry();
		}

		zip.close();
		out.close();
	}

	private static BarcodeRequest buildBarcodeRequest(String[] record) {
		LibMetrics.hitMethodRunCounter();

		String params = "";
		String type = "auto";
		if (record.length >= 2 && !record[1].equals("")) {
			type = record[1];
		}

		// size
		if (record.length >= 3 && !record[2].equals("")) {
			params += "size=" + record[2] + "&";
		}

		// dpi
		if (record.length >= 4 && !record[3].equals("")) {
			params += "dpi=" + record[3] + "&";
		}

		// extras
		if (record.length >= 5 && !record[4].equals("")) {
			params += record[4];
		}

		String uri = String.format(//
				"/api/%s/%s?", type, record[0], params);

		return new BarcodeRequest(uri);
	}

	private static ArrayList<CachedBarcode> generateBarcodes(ArrayList<BarcodeRequest> requests) {
		LibMetrics.hitMethodRunCounter();

		ArrayList<CachedBarcode> barcodes = new ArrayList<>();

		for (BarcodeRequest request : requests) {
			try {
				barcodes.add(BarcodeGenerator.requestBarcode(request));
			} catch (GenerationException e) {
				LibLog.log("server", "Failed to generate bulk barcode.");
			}
		}

		return barcodes;
	}
}
