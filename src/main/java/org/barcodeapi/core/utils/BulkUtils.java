package org.barcodeapi.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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

	public static ArrayList<CachedBarcode> getBarcodes(InputStream in) throws IOException, GenerationException {
		try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {

			ArrayList<BarcodeRequest> requests = new ArrayList<>();

			String[] record;
			while ((record = reader.readNext()) != null) {
				requests.add(buildBarcodeRequest(record));
			}
			reader.close();

			return generateBarcodes(requests);
		} catch (CsvValidationException e) {

			throw new GenerationException(ExceptionType.INVALID, e);
		}
	}

	public static void zipBarcodes(List<CachedBarcode> barcodes, OutputStream out) throws IOException, GenerationException {
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
	}

	private static BarcodeRequest buildBarcodeRequest(String[] record) throws GenerationException {

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

		String uri = String.format(//
				"/api/%s/%s?", type, record[0], params);

		return new BarcodeRequest(uri);
	}

	private static ArrayList<CachedBarcode> generateBarcodes(ArrayList<BarcodeRequest> requests)
			throws GenerationException {

		ArrayList<CachedBarcode> barcodes = new ArrayList<>();

		for (BarcodeRequest request : requests) {

			barcodes.add(BarcodeGenerator.requestBarcode(request));
		}

		return barcodes;
	}
}
