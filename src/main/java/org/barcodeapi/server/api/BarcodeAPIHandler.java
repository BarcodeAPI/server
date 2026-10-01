package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.Config;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RequestContext.Format;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * BarcodeAPIHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class BarcodeAPIHandler extends RestHandler {

	private static final int CACHED_LIFE_MIN = Config.get()//
			.getJSONObject("client").getInt("cacheBarcode");

	private static final int CACHED_LIFE_SEC = (CACHED_LIFE_MIN * 60);

	private static final int CACHED_LIFE_MS = (CACHED_LIFE_SEC * 1000);

	private final String cacheControl = String.format("max-age=%d, public", CACHED_LIFE_SEC);

	public BarcodeAPIHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		BarcodeRequest request = null;
		CachedBarcode barcode = null;
		Format format = Format.PNG;
		byte[] bytes = null;

		try {

			// Parse the request
			request = BarcodeRequest.fromURI(c.getUri());

			// Generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(request);

			// Determine output format based on request
			for (Format formatRequested : c.getFormats()) {

				switch (formatRequested) {

				case JSON:
					// Output as JSON encoded (base64)
					format = Format.JSON;
					bytes = barcode.encodeJSON().getBytes();
					break;

				case TEXT:
				case HTML:
					// Output as HTML page
					format = Format.HTML;
					bytes = barcode.encodeHTML().getBytes();
					break;

				case ANY:
				case PNG:
				default:
					// Output as PNG image
					format = Format.PNG;
					bytes = barcode.getBarcodeData();
					break;
				}
			}

			// Set response code okay
			r.setStatus(HttpServletResponse.SC_OK);

			// Add cache headers
			if (CACHED_LIFE_MIN > 0) {
				r.setDateHeader("Expires", //
						(System.currentTimeMillis() + (CACHED_LIFE_MS)));
				r.setHeader("Cache-Control", cacheControl);
			}

			// File save-as name / force download
			boolean download = (c.getRequest().getHeader("X-ForceDownload") != null);
			r.setHeader("Content-Disposition", ((download) ? "attachment; " : "") + //
					("filename=" + (barcode.getBarcodeStringEncoded() + format.getExt())));

		} catch (GenerationException e) {

			// Log the generation failure (if not empty)
			if (!(e.getExceptionType() == ExceptionType.EMPTY)) {
				LibLog._clogF("E6009", c.getUri(), e.getMessage());
			}

			// Set status headers for failure
			r.setStatus(e.getExceptionType().getStatusCode());
			r.setHeader("X-Error-Message", e.getCause().getMessage());

			// Replace barcode with failure barcode
			barcode = e.getExceptionType().getBarcodeImage();

			// Assign output fields
			format = Format.PNG;
			bytes = barcode.getBarcodeData();
		} catch (Exception | Error e) {

			LibLog._log("Unhandled exception!", e);
		}

		// Add content headers type and length
		r.setContentType(format.getMime());
		r.setHeader("Content-Length", Long.toString(bytes.length));

		// Add the barcode type and detail headers
		r.setHeader("X-Barcode-Type", barcode.getBarcodeType().getName());
		r.setHeader("X-Barcode-Content", barcode.getBarcodeStringEncoded());

		// Write the data to the stream
		r.getOutputStream().write(bytes);
	}
}
