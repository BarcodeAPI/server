package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONException;

public class BarcodeAPIHandler extends RestHandler {

	private final CachedBarcode ERR;
	private final CachedBarcode BLK;

	public BarcodeAPIHandler() {
		super();

		try {

			ERR = BarcodeGenerator.requestBarcode(new BarcodeRequest(//
					"/128/$$@E$$@R$$@R$$@O$$@R$$@"));
			BLK = BarcodeGenerator.requestBarcode(new BarcodeRequest(//
					"/128/$$@B$$@L$$@A$$@C$$@K$$@L$$@I$$@S$$@T$$@"));
		} catch (GenerationException e) {
			throw new RuntimeException("init failed");
		}
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		CachedBarcode barcode;

		try {

			// generate user requested barcode
			BarcodeRequest barcodeRequest = new BarcodeRequest(uri);
			barcode = BarcodeGenerator.requestBarcode(barcodeRequest);

		} catch (GenerationException e) {

			Log.out(LOG.ERROR, "" + //
					"Failed [ " + uri + " ] " + //
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
