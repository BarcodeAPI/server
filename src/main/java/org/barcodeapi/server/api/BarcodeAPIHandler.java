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
	protected void onRequest(String uri, HttpServletRequest srvReq, HttpServletResponse response)
			throws JSONException, IOException {

		CachedBarcode barcode;
		BarcodeRequest request = new BarcodeRequest(uri);

		try {
			// generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(request);

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

		// add cache headers
		response.setHeader("Cache-Control", "max-age=86400, public");

		// barcode details
		response.setHeader("X-Barcode-Type", barcode.getType().toString());
		response.setHeader("X-Barcode-Content", barcode.getEncoded());

		// file save-as name / force download
		boolean download = request.getOptions().optBoolean("download");
		response.setHeader("Content-Disposition", //
				((download) ? "attachment; " : "") + //
						("filename=" + barcode.getNice() + ".png"));

		// add content headers and write data to stream
		response.setHeader("Content-Type", "image/png");
		response.setHeader("Content-Length", Long.toString(barcode.getDataSize()));
		response.getOutputStream().write(barcode.getData());
	}
}
