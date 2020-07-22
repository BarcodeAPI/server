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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class BulkUtils {

	public static void getZippedBarcodes(int max, InputStream in, OutputStream out)
			throws IOException, GenerationException {

		try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {

			ArrayList<BarcodeRequest> requests = new ArrayList<>();

			String[] record;
			while ((record = reader.readNext()) != null) {
				if (requests.size() < max) {
					requests.add(BarcodeRequest.fromCSV(record));
				} else {
					break;
				}
			}
			reader.close();

			ArrayList<CachedBarcode> barcodes = generateBarcodes(requests);

			ZipOutputStream zip = new ZipOutputStream(out);

			for (CachedBarcode barcode : barcodes) {

				String niceData = barcode.getProperties().getProperty("nice");
				ZipEntry zipEntry = new ZipEntry(niceData + ".png");
				zip.putNextEntry(zipEntry);
				zip.write(barcode.getData(), 0, barcode.getDataSize());
				zip.closeEntry();
			}

			zip.close();
			out.close();

		} catch (CsvValidationException e) {

			throw new GenerationException(ExceptionType.INVALID, e);
		}
	}

	public static ArrayList<CachedBarcode> generateBarcodes(ArrayList<BarcodeRequest> requests)
			throws GenerationException {

		ArrayList<CachedBarcode> barcodes = new ArrayList<>();

		for (BarcodeRequest request : requests) {

			barcodes.add(BarcodeGenerator.requestBarcode(request));
		}

		return barcodes;
	}
}
