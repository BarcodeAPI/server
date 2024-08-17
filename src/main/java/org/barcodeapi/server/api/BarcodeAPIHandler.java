package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONException;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * BarcodeAPIHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeAPIHandler extends RestHandler {

	private static final int CACHED_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("barcode").getInt("client");

	private final String cacheControl = String.format("max-age=%d, public", CACHED_LIFE);

	public BarcodeAPIHandler() {
		super(
				// Authentication not required
				false,
				// Use client rate limit
				true,
				// Do not create new sessions
				false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		CachedBarcode barcode = null;
		BarcodeRequest request = new BarcodeRequest(c.getUri());

		try {

			// Calculate token cost
			int cost = request.getCost();

			// Send token cost to user
			r.setHeader("X-RateLimit-Cost", Integer.toString(cost));

			// Try to spend tokens
			if (!c.getLimiter().spendTokens(cost)) {

				// Return rate limited barcode to user
				throw new GenerationException(ExceptionType.LIMITED);
			}

			// Generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(request);

			// Set response code okay
			r.setStatus(HttpServletResponse.SC_OK);

			// Add cache headers
			if (request.useCache()) {
				r.setDateHeader("Expires", //
						(System.currentTimeMillis() + (CACHED_LIFE * 1000)));
				r.setHeader("Cache-Control", cacheControl);
			}

		} catch (GenerationException e) {

			// Log the generation failure
			LibLog._clogF("E6009", c.getUri(), e.getMessage());

			// Set status headers for failure
			r.setStatus(e.getExceptionType().getStatusCode());
			r.setHeader("X-Error-Message", e.getMessage());

			// Replace barcode with failure image
			barcode = e.getExceptionType().getBarcodeImage();
		}

		// Add the code type and detail headers
		r.setHeader("X-Barcode-Type", barcode.getBarcodeType());
		r.setHeader("X-Barcode-Content", barcode.getBarcodeStringEncoded());

		// Determine output format to send
		switch (request.getOptions().optString("format", "png")) {

		// Serve as PNG
		case "png":

			// file save-as name / force download
			boolean download = request.getOptions().optBoolean("download");
			r.setHeader("Content-Disposition", //
					((download) ? "attachment; " : "") + //
							("filename=" + barcode.getBarcodeStringNice() + ".png"));

			// add content headers and write data to stream
			r.setCharacterEncoding(null);
			r.setHeader("Content-Type", "image/png");
			r.setHeader("Content-Length", Long.toString(barcode.getBarcodeDataSize()));
			r.getOutputStream().write(barcode.getBarcodeData());
			break;

		// Serve as base64
		case "b64":

			// Get data as base64 encoded string
			byte[] encodedBytes = barcode.encodeBase64().getBytes();

			// Add content headers and write data to stream
			r.setHeader("Content-Type", "image/png");
			r.setHeader("Content-Encoding", "base64");
			r.setHeader("Content-Length", Long.toString(encodedBytes.length));
			r.getOutputStream().write(encodedBytes);
			break;

		// Serve as JSON
		case "json":

			// Get data as JSON encoded string
			byte[] encodedJson = barcode.encodeJSON().getBytes();

			// Add content headers and write data to stream
			r.setHeader("Content-Type", "application/json");
			r.setHeader("Content-Length", Long.toString(encodedJson.length));
			r.getOutputStream().write(encodedJson);
			break;

		// Unknown output format
		default:
			r.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			r.setHeader("X-Error-Message", "Invalid output format.");
			break;
		}
	}
}
