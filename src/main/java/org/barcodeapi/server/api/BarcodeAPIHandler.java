package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.eclipse.jetty.server.Request;

public class BarcodeAPIHandler extends RestHandler {

	private final CachedBarcode ERR;
	private final CachedBarcode BLK;

	public BarcodeAPIHandler() {

		try {

			ERR = BarcodeGenerator.requestBarcode("/128/$$@E$$@R$$@R$$@O$$@R$$@");
			BLK = BarcodeGenerator.requestBarcode("/128/$$@B$$@L$$@A$$@C$$@K$$@L$$@I$$@S$$@T$$@");
		} catch (GenerationException e) {
			throw new RuntimeException("init failed");
		}
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		CachedBarcode barcode;
		try {

			// generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(baseRequest.getOriginalURI());

		} catch (GenerationException e) {

			Log.out(LOG.ERROR, "" + //
					"Failed [ " + target + " ] " + //
					"reason [ " + e.getMessage() + " ]");

			switch (e.getExceptionType()) {
			case BLACKLIST:
				// serve blacklist code
				barcode = BLK;
				break;

			case EMPTY:
			case FAILED:
			default:
				// serve error code
				barcode = ERR;
				break;
			}

			// set HTTP response code and add message to headers
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("X-Error-Message", e.getMessage());
		}

		// additional properties
		String type = barcode.getProperties().getProperty("type");
		String nice = barcode.getProperties().getProperty("nice");
		String encd = barcode.getProperties().getProperty("encd");

		// add cache headers
		response.setHeader("Cache-Control", "max-age=86400, public");

		// add content headers
		response.setHeader("Content-Type", "image/png");
		response.setHeader("Content-Length", Long.toString(barcode.getDataSize()));

		// file name when clicking save
		response.setHeader("Content-Disposition", "filename=" + nice + ".png");

		// barcode details
		response.setHeader("X-Barcode-Type", type);
		response.setHeader("X-Barcode-Content", encd);

		// print image to stream
		response.getOutputStream().write(barcode.getData());
	}
}
