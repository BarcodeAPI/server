package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.barcodeapi.core.utils.BulkUtils;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;

/**
 * BulkHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BulkHandler extends RestHandler {

	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement("./");

	public BulkHandler() {
		super(false, true);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws ServletException, IOException {

		// Setup accept multi-part
		c.getRequest().setAttribute(Request.MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		if (!c.getRequest().getContentType().startsWith("multipart/")) {
			return;
		}

		// Response headers for file download
		r.setHeader("Content-Type", "application/zip");
		r.setHeader("Content-Disposition", "filename=barcodes.zip");

		try {

			// Get the uploaded file
			Part part = c.getRequest().getPart("csvFile");

			// Pass input and output streams to bulk helper
			BulkUtils.getZippedBarcodes(250, //
					part.getInputStream(), r.getOutputStream());

		} catch (GenerationException e) {

			e.printStackTrace();
		}
	}
}
