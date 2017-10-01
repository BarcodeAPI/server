package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.barcodeapi.server.gen.types.Code128Generator;
import org.barcodeapi.server.gen.types.Code39Generator;
import org.barcodeapi.server.gen.types.DataMatrixGenerator;
import org.barcodeapi.server.gen.types.Ean13Generator;
import org.barcodeapi.server.gen.types.Ean8Generator;
import org.barcodeapi.server.gen.types.QRCodeGenerator;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class BarcodeAPIHandler extends AbstractHandler {

	HashMap<CodeType, CodeGenerator> codeGenerators;

	String serverName;

	public BarcodeAPIHandler() {

		codeGenerators = new HashMap<CodeType, CodeGenerator>();

		codeGenerators.put(CodeType.EAN8, new Ean8Generator());
		codeGenerators.put(CodeType.EAN13, new Ean13Generator());
		codeGenerators.put(CodeType.Code39, new Code39Generator());
		codeGenerators.put(CodeType.Code128, new Code128Generator());
		codeGenerators.put(CodeType.QRCode, new QRCodeGenerator());
		codeGenerators.put(CodeType.DataMatrix, new DataMatrixGenerator());

		try {

			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		// time of the request
		long timeNow = System.currentTimeMillis();

		// get the request string
		String data = target.substring(1, target.length());

		// use cache if within threshold
		boolean useCache = data.length() <= 64;

		// process selected type
		CodeType type;
		int typeIndex = data.indexOf("/");
		if (typeIndex > 0) {

			String typeString = target.substring(1, typeIndex + 1);

			type = CodeType.fromString(typeString);

			if (type == null) {

				type = CodeType.getType(data);
			} else {

				data = data.substring(typeIndex + 1);
			}
		} else {

			type = CodeType.getType(data);
		}

		if (data == null || data.equals("")) {

			System.out.println("Empty request.");

			baseRequest.setHandled(true);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("Server", "BarcodeAPI.org");

			response.getOutputStream().println("Empty request.");
			return;
		}

		// image object
		CachedObject barcode = null;

		// lookup in cache if allowed
		if (useCache) {

			barcode = BarcodeCache.getInstance().getBarcode(type, data);
		}

		// if not found in cache
		if (barcode == null) {

			// render image
			long start = System.currentTimeMillis();
			byte[] image = codeGenerators.get(type).getCode(data);
			double renderTime = System.currentTimeMillis() - start;

			// add to total render time
			StatsCollector.getInstance().incrementCounter("system.renderTime", renderTime);

			// fail if render failed
			if (image == null) {

				System.out.println("Failed to render [ " + data + " ]");

				baseRequest.setHandled(true);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.setHeader("Server", "BarcodeAPI.org");

				response.getOutputStream().println("Failed to render [ " + data + " ]");
				return;
			}

			System.out.println(timeNow + //
					" : Rendered [ " + type.toString() + " ] with [ " + data + " ] in [ " + renderTime + "ms ]");

			// add data to image object
			barcode = new CachedObject(image);

			// add to cache if allowed
			if (useCache) {

				BarcodeCache.getInstance().addImage(type, data, barcode);
			}
		} else {

			System.out.println(timeNow + //
					" : Served [ " + type.toString() + " ] with [ " + data + " ] from cache");
		}

		// set response okay
		baseRequest.setHandled(true);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Server", "BarcodeAPI.org");

		// add headers describing request
		response.setHeader("X-RequestTime", Long.toString(timeNow));
		response.setHeader("X-CodeServer", serverName);
		response.setHeader("X-CodeType", type.toString());
		response.setHeader("X-CodeData", data);
		response.setHeader("Content-Type", "image/jpg");
		response.setHeader("Content-Length", Long.toString(barcode.getDataSize()));

		// print data to stream
		response.getOutputStream().write(barcode.getData());
	}
}
