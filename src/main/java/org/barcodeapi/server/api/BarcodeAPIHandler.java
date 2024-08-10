package org.barcodeapi.server.api;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

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

	private final CachedBarcode INV;
	private final CachedBarcode FLD;
	private final CachedBarcode BLK;
	private final CachedBarcode RTE;
	private final CachedBarcode BSY;

	public BarcodeAPIHandler() {
		super(
				// Authentication not required
				false,
				// Use client rate limit
				true,
				// Do not create new sessions
				false);

		try {

			INV = BarcodeGenerator.requestBarcode(//
					BarcodeRequest.fromURI("/128/$$@INVALID$$@"));
			FLD = BarcodeGenerator.requestBarcode(//
					BarcodeRequest.fromURI("/128/$$@F$$@A$$@I$$@L$$@E$$@D$$@"));
			BLK = BarcodeGenerator.requestBarcode(//
					BarcodeRequest.fromURI("/128/$$@B$$@L$$@A$$@C$$@K$$@L$$@I$$@S$$@T$$@"));
			RTE = BarcodeGenerator.requestBarcode(//
					BarcodeRequest.fromURI("/128/$$@RATE$$@$$@LIMIT$$@"));
			BSY = BarcodeGenerator.requestBarcode(//
					BarcodeRequest.fromURI("/128/$$@B$$@U$$@S$$@Y$$@"));
		} catch (GenerationException e) {

			throw LibLog._clog("E0789", e)//
					.asException(IllegalStateException.class);
		}
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		CachedBarcode barcode = null;
		BarcodeRequest request = BarcodeRequest.fromURI(c.getUri());

		try {

			// Calculate token cost
			int cost = (request.useCache()) ? //
					request.getType().getCostBasic() : request.getType().getCostCustom();

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

		} catch (GenerationException e) {

			// Log barcode generation failures
			LibLog._clogF("E6009", c.getUri(), e.getMessage());
			r.setHeader("X-Error-Message", e.getMessage());

			switch (e.getExceptionType()) {

			// Send rate limit response code
			case LIMITED:
				barcode = RTE;
				r.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
				r.setHeader("X-ClientRateLimited", "YES");
				break;

			// Serve blacklist response code
			case BLACKLIST:
				barcode = BLK;
				r.setStatus(HttpServletResponse.SC_FORBIDDEN);
				break;

			// Send bad request response code
			case EMPTY:
			case INVALID:
				barcode = INV;
				r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				break;

			// Send server busy response code
			case BUSY:
				barcode = BSY;
				r.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				break;

			// Send server error response code
			case FAILED:
				barcode = FLD;
				r.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				break;
			}
		}

		// Add cache headers
		if (request.useCache()) {
			long tplus = 86400;
			long expires = System.currentTimeMillis() + (tplus * 1000);
			r.setDateHeader("Expires", expires);
			r.setHeader("Cache-Control", String.format("max-age=%d, public", tplus));
		}

		// Add type and barcode detail headers
		r.setHeader("X-Barcode-Type", barcode.getType().getName());
		r.setHeader("X-Barcode-Content", barcode.getEncoded());

		switch (request.getOptions().optString("format", "png")) {

		// Serve as base64
		case "b64":

			// encode as string
			String encoded = Base64.getEncoder()//
					.encodeToString(barcode.getData());
			byte[] encodedBytes = encoded.getBytes();

			// add content headers and write data to stream
			r.setHeader("Content-Type", "image/png");
			r.setHeader("Content-Encoding", "base64");
			r.setHeader("Content-Length", Long.toString(encodedBytes.length));
			r.getOutputStream().write(encodedBytes);
			break;

		// Serve as PNG
		case "png":

			// file save-as name / force download
			boolean download = request.getOptions().optBoolean("download");
			r.setHeader("Content-Disposition", //
					((download) ? "attachment; " : "") + //
							("filename=" + barcode.getNice() + ".png"));

			// add content headers and write data to stream
			r.setCharacterEncoding(null);
			r.setHeader("Content-Type", "image/png");
			r.setHeader("Content-Length", Long.toString(barcode.getDataSize()));
			r.getOutputStream().write(barcode.getData());
			break;

		// Unknown formats
		default:
			// unknown output format
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("X-Error-Message", "Invalid output format.");
			break;
		}
	}
}
