package org.barcodeapi.core.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BulkUtils {

	public static ArrayList<CachedBarcode> getBarcodesFromJson(InputStream in) throws GenerationException {
		String input = new BufferedReader(
			new InputStreamReader(in, StandardCharsets.UTF_8))
			.lines()
			.collect(Collectors.joining("\n"));
		JSONArray json;
		if (input.startsWith("[")) {
			json = new JSONArray(input);
		} else if (input.startsWith("{")) {
			json = new JSONArray();
			json.put(new JSONObject(input));
		} else {
			throw new GenerationException(ExceptionType.INVALID, "invalid json");
		}

		ArrayList<BarcodeRequest> requests = new ArrayList<>();
		for (int i = 0; i < json.length(); i++) {
			if (!(json.get(i) instanceof JSONObject)) {
				throw new GenerationException(ExceptionType.INVALID, "requested item at index " + i + " is not an object");
			}
			requests.add(BarcodeRequest.fromJson((JSONObject) json.get(i)));
		}
		return generateBarcodes(requests);
	}

	public static ArrayList<CachedBarcode> getBarcodesFromCsv(InputStream in) throws IOException, GenerationException {
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
