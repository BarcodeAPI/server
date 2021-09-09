package org.barcodeapi.server.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.barcodeapi.core.utils.BulkUtils;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class BulkHandler extends RestHandler {

	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement("./");

	public BulkHandler() {
		super();
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Response headers for file download
		response.setHeader("Content-Type", "application/zip");
		response.setHeader("Content-Disposition", "filename=barcodes.zip");

		try {
			String contentType = request.getContentType();
			if (contentType == null) {
				contentType = "";
			}

			List<CachedBarcode> generatedBarcodes;

			if (contentType.startsWith("multipart/")) {
				// Setup accept multi-part
				request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);

				// Get the uploaded file
				Part part = request.getPart("csvFile");

				// Pass input and output streams to bulk helper
				generatedBarcodes = BulkUtils.getBarcodes(part.getInputStream());
			} else {
				generatedBarcodes = BulkUtils.getBarcodes(request.getInputStream());
			}

			String acceptHeader = request.getHeader("Accept");
			if (acceptHeader == null) {
				acceptHeader = "*/*"; // Accept all
			}

			if (acceptHeader.contains("application/zip") || acceptHeader.contains("*/zip")) {
				BulkUtils.zipBarcodes(generatedBarcodes, response.getOutputStream());
			} else {
				JSONObject root = new JSONObject();
				JSONArray items = new JSONArray();
				for (CachedBarcode barcode : generatedBarcodes) {
					JSONObject jsonBarcode = new JSONObject();
					jsonBarcode.put("barcode", Base64.getEncoder().encodeToString(barcode.getData()));
					jsonBarcode.put("nice", barcode.getProperties().get("nice"));
					jsonBarcode.put("data", barcode.getProperties().get("data"));
					jsonBarcode.put("type", barcode.getProperties().get("type"));
					items.put(jsonBarcode);
				}
				root.put("barcodes", items);
				response.getOutputStream().write(root.toString().getBytes(StandardCharsets.UTF_8));
				response.setHeader("Content-Type", "application/json");
			}


		} catch (GenerationException e) {

			e.printStackTrace();
		}
	}
}
