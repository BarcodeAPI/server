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

	private static final int CACHED_LIFE_MIN = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("barcode").getInt("client");

	private static final int CACHED_LIFE_SEC = (CACHED_LIFE_MIN * 60);

	private static final int CACHED_LIFE_MS = (CACHED_LIFE_SEC * 1000);

	private final String cacheControl = String.format("max-age=%d, public", CACHED_LIFE_SEC);

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

		BarcodeRequest request = null;
		CachedBarcode barcode = null;

		try {

			// Parse the request
			request = BarcodeRequest.fromURI(c.getUri());

			// Send token cost to user
			r.setHeader("X-RateLimit-Cost", //
					Integer.toString(request.getCost()));

			// Try to spend tokens
			if (!c.getLimiter().spendTokens(request.getCost())) {

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
						(System.currentTimeMillis() + (CACHED_LIFE_MS)));
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

		switch (c.getFormat()) {

		// Serve as JSON
		case JSON:

			// Get data as JSON encoded string
			byte[] encodedJson = barcode.encodeJSON().getBytes();

			// Add content headers and write data to stream
			r.setHeader("Content-Type", c.getFormat().getMime());
			r.setHeader("Content-Length", Long.toString(encodedJson.length));
			r.getOutputStream().write(encodedJson);
			break;

		// Serve as PNG
		case AS_REQUIRED:
		case PNG:

			// Determine if response is B64 encoded
			boolean asB64 = (c.getEncoding() == RequestContext.Encoding.BASE64);
			byte[] bytes = (asB64) ? barcode.encodeBase64().getBytes() : barcode.getBarcodeData();

			// Add content length and type headers
			r.setHeader("Content-Length", Long.toString(bytes.length));
			r.setHeader("Content-Type", (RequestContext.Format.PNG.getMime() + ((asB64) ? ";base64" : "")));

			// File save-as name / force download
			boolean download = (c.getRequest().getHeader("X-ForceDownload") != null);
			r.setHeader("Content-Disposition", //
					((download) ? "attachment; " : "") + //
							("filename=" + barcode.getBarcodeStringNice() + ".png"));

			// Write data to stream
			r.getOutputStream().write(bytes);
			break;

		// Unknown output format
		default:
			r.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			r.setHeader("X-Error-Message", "Invalid output format.");
			break;
		}
	}
}
