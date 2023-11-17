package org.barcodeapi.server.api;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.RateLimitException;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONException;

import com.mclarkdev.tools.liblog.LibLog;

public class BarcodeAPIHandler extends RestHandler {

	private final CachedBarcode ERR;
	private final CachedBarcode EXC;
	private final CachedBarcode BLK;
	private final CachedBarcode RTE;

	public BarcodeAPIHandler() {
		super(false, true);

		try {

			ERR = BarcodeGenerator.requestBarcode(BarcodeRequest.fromURI(//
					"/128/$$@E$$@R$$@R$$@O$$@R$$@"));
			EXC = BarcodeGenerator.requestBarcode(BarcodeRequest.fromURI(//
					"/128/$$@F$$@A$$@I$$@L$$@E$$@D$$@"));
			BLK = BarcodeGenerator.requestBarcode(BarcodeRequest.fromURI(//
					"/128/$$@B$$@L$$@A$$@C$$@K$$@L$$@I$$@S$$@T$$@"));
			RTE = BarcodeGenerator.requestBarcode(BarcodeRequest.fromURI(//
					"/128/$$@RATE$$@$$@LIMIT$$@"));
		} catch (GenerationException e) {

			throw LibLog._clog("E0789", e).asException(IllegalStateException.class);
		}
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		CachedBarcode barcode = null;
		BarcodeRequest request = BarcodeRequest.fromURI(ctx.getUri());

		try {

			try {

				// try to spend tokens
				ctx.getLimiter().spendTokens(request.getCost());
			} catch (RateLimitException e) {

				// return rate limited barcode to user
				throw new GenerationException(ExceptionType.LIMITED);
			}

			// generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(request);

		} catch (GenerationException e) {

			// log barcode generation failures
			LibLog._clogF("E6009", ctx.getUri(), e.getMessage());
			response.setHeader("X-Error-Message", e.getMessage());

			switch (e.getExceptionType()) {
			case LIMITED:
				// serve rate limit code
				barcode = RTE;
				response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
				break;

			case BLACKLIST:
				// serve blacklist code
				barcode = BLK;
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				break;

			case INVALID:
			case EMPTY:
				// serve error code
				barcode = ERR;
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				break;

			case FAILED:
				// serve error code
				barcode = EXC;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				break;
			}

		}

		// add cache headers
		if (request.useCache()) {
			response.setHeader("Cache-Control", "max-age=86400, public");
		}

		// barcode details
		response.setHeader("X-Barcode-Type", barcode.getType().toString());
		response.setHeader("X-Barcode-Content", barcode.getEncoded());

		switch (request.getOptions().optString("format", "png")) {
		case "b64": // serve as base64
			String encoded = Base64.getEncoder().encodeToString(barcode.getData());
			byte[] encodedBytes = encoded.getBytes();
			response.setHeader("Content-Type", "image/png");
			response.setHeader("Content-Encoding", "base64");
			response.setHeader("Content-Length", Long.toString(encodedBytes.length));
			response.getOutputStream().write(encodedBytes);
			break;

		case "png": // serve as PNG

			// file save-as name / force download
			boolean download = request.getOptions().optBoolean("download");
			response.setHeader("Content-Disposition", //
					((download) ? "attachment; " : "") + //
							("filename=" + barcode.getNice() + ".png"));

			// add content headers and write data to stream
			response.setHeader("Content-Type", "image/png");
			response.setHeader("Content-Length", Long.toString(barcode.getDataSize()));
			response.getOutputStream().write(barcode.getData());
			break;

		default:
			// unknown output format
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("X-Error-Message", "Invalid output format.");
			break;
		}
	}
}
