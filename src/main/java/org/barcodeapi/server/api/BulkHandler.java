package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.barcodeapi.core.utils.BulkUtils;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;

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

			if (contentType.startsWith("multipart/")) {
				// Setup accept multi-part
				request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);

				// Get the uploaded file
				Part part = request.getPart("csvFile");

				// Pass input and output streams to bulk helper
				BulkUtils.getZippedBarcodes(250, part.getInputStream(), response.getOutputStream());
			} else {
				BulkUtils.getZippedBarcodes(250, request.getInputStream(), response.getOutputStream());
			}


		} catch (GenerationException e) {

			e.printStackTrace();
		}
	}
}
