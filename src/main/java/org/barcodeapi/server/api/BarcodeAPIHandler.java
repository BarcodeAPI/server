package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RequestContext.Format;
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
			.getJSONObject("client").getInt("cacheBarcode");

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
		Format format = Format.PNG;
		byte[] bytes = null;

		try {

			// Parse the request
			request = BarcodeRequest.fromURI(c.getUri());

			// Send token cost to user
			r.setHeader("X-RateLimit-Cost", //
					Integer.toString(request.getCost()));

			// Try to spend tokens
			if (!c.getLimiter().spendTokens(request.getCost())) {

				// Return rate limited barcode to user
				throw new GenerationException(ExceptionType.LIMITED, //
						new Throwable("Client is rate limited, try again later."));
			}

			// Generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(request);

			// Determine output format
			for (Format f : c.getFormats()) {
				if (f.equals(Format.JSON)) {
					// Encode data as JSON
					format = f;
					bytes = barcode.encodeJSON().getBytes();
				}
			}

			// Set response code okay
			r.setStatus(HttpServletResponse.SC_OK);

			// Add cache headers
			if (request.useCache()) {
				r.setDateHeader("Expires", //
						(System.currentTimeMillis() + (CACHED_LIFE_MS)));
				r.setHeader("Cache-Control", cacheControl);
			}

			// File save-as name / force download
			boolean download = (c.getRequest().getHeader("X-ForceDownload") != null);
			r.setHeader("Content-Disposition", ((download) ? "attachment; " : "") + //
					("filename=" + (barcode.getBarcodeStringEncoded() + format.getExt())));

		} catch (GenerationException e) {

			// Log the generation failure
			LibLog._clogF("E6009", c.getUri(), e.getMessage());

			// Set status headers for failure
			r.setStatus(e.getExceptionType().getStatusCode());
			r.setHeader("X-Error-Message", e.getCause().getMessage());

			// Replace barcode with failure barcode
			barcode = e.getExceptionType().getBarcodeImage();
		} finally {

			// Get barcode data if not already set
			bytes = ((bytes == null) ? barcode.getBarcodeData() : bytes);
		}

		// Add content headers type and length
		r.setHeader("Content-Type", format.getMime());
		r.setHeader("Content-Length", Long.toString(bytes.length));

		// Add the barcode type and detail headers
		r.setHeader("X-Barcode-Type", barcode.getBarcodeType().getName());
		r.setHeader("X-Barcode-Content", barcode.getBarcodeStringEncoded());

		// Write the data to the stream
		r.getOutputStream().write(bytes);
	}
}
