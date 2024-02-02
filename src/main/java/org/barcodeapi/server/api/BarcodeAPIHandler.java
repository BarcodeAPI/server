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

public class BarcodeAPIHandler extends RestHandler {

	private final CachedBarcode ERR;
	private final CachedBarcode EXC;
	private final CachedBarcode BLK;
	private final CachedBarcode RTE;
	private final CachedBarcode BSY;

	public BarcodeAPIHandler() {
		super(false, true);

		try {

			ERR = BarcodeGenerator.requestBarcode(//
					BarcodeRequest.fromURI("/128/$$@E$$@R$$@R$$@O$$@R$$@"));
			EXC = BarcodeGenerator.requestBarcode(//
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
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		CachedBarcode barcode = null;
		BarcodeRequest request = BarcodeRequest.fromURI(ctx.getUri());

		try {

			// calculate token cost
			int cost = (request.useCache()) ? //
					request.getType().getCostBasic() : request.getType().getCostCustom();

			// send token cost to user
			response.setHeader("X-RateLimit-Cost", Integer.toString(cost));

			// try to spend tokens
			if (!ctx.getLimiter().spendTokens(cost)) {

				// return rate limited barcode to user
				throw new GenerationException(ExceptionType.LIMITED);
			}

			// generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(request);

			// set response code okay
			response.setStatus(HttpServletResponse.SC_OK);

		} catch (GenerationException e) {

			// log barcode generation failures
			LibLog._clogF("E6009", ctx.getUri(), e.getMessage());
			response.setHeader("X-Error-Message", e.getMessage());

			switch (e.getExceptionType()) {
			case LIMITED:
				// send rate limit response code
				barcode = RTE;
				response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
				response.setHeader("X-ClientRateLimited", "YES");
				break;

			case BLACKLIST:
				// serve blacklist response code
				barcode = BLK;
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				break;

			case EMPTY:
			case INVALID:
				// send internal server response code
				barcode = ERR;
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				break;

			case BUSY:
				// send server busy response code
				barcode = BSY;
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				break;

			case FAILED:
				// send server error response code
				barcode = EXC;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				break;
			}
		}

		// add cache headers
		if (request.useCache()) {
			long tplus = 86400;
			long expires = System.currentTimeMillis() + (tplus * 1000);
			response.setDateHeader("Expires", expires);
			response.setHeader("Cache-Control", String.format("max-age=%d, public", tplus));
		}

		// add type and barcode detail headers
		response.setHeader("X-Barcode-Type", barcode.getType().getName());
		response.setHeader("X-Barcode-Content", barcode.getEncoded());

		switch (request.getOptions().optString("format", "png")) {

		case "b64": // serve as base64

			// encode as string
			String encoded = Base64.getEncoder()//
					.encodeToString(barcode.getData());
			byte[] encodedBytes = encoded.getBytes();

			// add content headers and write data to stream
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
			response.setCharacterEncoding(null);
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
