package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.session.SessionObject;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class BarcodeAPIHandler extends AbstractHandler {

	private String serverName;

	private CodeGenerators generators;
	private SessionCache sessions;

	public BarcodeAPIHandler() {

		generators = CodeGenerators.getInstance();
		sessions = SessionCache.getInstance();

		try {

			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		SessionObject session = sessions.getSession(baseRequest);

		// time of the request
		long requestTime = System.currentTimeMillis();

		try {

			// get the request string
			String requestURI = request.getRequestURI();
			String data = requestURI.substring(1, requestURI.length());
			data = URLDecoder.decode(data, "UTF-8");
			session.onRender(data);

			// use cache if within threshold
			boolean useCache = data.length() <= 64;

			// process selected type
			CodeGenerator generator;
			CodeType type;

			int typeIndex = data.indexOf("/");
			if (typeIndex > 0) {

				String typeString = target.substring(1, typeIndex + 1);

				generator = generators.getGenerator(typeString);
				type = generator.getType();

				if (type == null) {

					type = TypeSelector.getType(data);
				} else {

					data = data.substring(typeIndex + 1);
				}
			} else {

				type = TypeSelector.getType(data);
				generator = generators.getGenerator(type);
			}

			if (data == null || data.equals("")) {

				System.out.println(requestTime + " : Empty request.");

				baseRequest.setHandled(true);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.setHeader("Server", "BarcodeAPI.org");

				response.getOutputStream().println("Empty request.");
				return;
			}

			// build a header safe data response
			String dataEncoded = URLEncoder.encode(data, "UTF-8");
			String fileName = "barcode";

			// image object
			CachedObject barcode = null;

			// lookup in cache if allowed
			if (useCache) {

				barcode = BarcodeCache.getInstance().getBarcode(type, data);
			}

			// if not found in cache
			if (barcode == null) {

				try {

					// render image
					double start = System.nanoTime();
					byte[] image = generator.getCode(data);
					double renderTime = (System.nanoTime() - start) / 1000 / 1000;
					String length = String.format("%.2f", renderTime);

					// add to total render time
					StatsCollector.getInstance().incrementCounter("system.renderTime", renderTime);

					System.out.println(requestTime + " :" + //
							" Rendered [ " + type.toString() + " ] " + //
							" with [ " + data + " ]" + //
							" in [ " + length + "ms ]" + //
							" size [ " + image.length + "B ]");

					// create new object with image
					barcode = new CachedObject(image);

					// add to cache if allowed
					if (useCache) {

						BarcodeCache.getInstance().addImage(type, data, barcode);
					}
				} catch (Exception e) {

					String message = "Failed [ " + type.toString() + " ]" + //
							" with [ " + data + " ]" + //
							" reason [ " + e.getMessage() + " ]";

					System.out.println(requestTime + " : " + message);

					baseRequest.setHandled(true);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setHeader("Server", "BarcodeAPI.org");

					response.getOutputStream().println(message);
					return;
				}
			} else {

				System.out.println(requestTime + //
						" : Served [ " + type.toString() + " ] with [ " + data + " ] from cache");
			}

			// set response okay
			baseRequest.setHandled(true);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Server", "BarcodeAPI.org");
			response.addHeader("Set-Cookie", "session=" + session.getKey() + ";");

			// add character set
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Accept-Charset", "utf-8");

			// add cache headers
			response.setHeader("Cache-Control", "max-age=86400, public");

			// add headers describing request
			response.setHeader("X-RequestTime", Long.toString(requestTime));
			response.setHeader("X-CodeServer", serverName);
			response.setHeader("X-CodeType", type.toString());
			response.setHeader("X-CodeData", dataEncoded);
			response.setHeader("X-CodeHash", barcode.getChecksum());

			// add content headers
			response.setHeader("Content-Type", "image/png");
			response.setHeader("Content-Length", Long.toString(barcode.getDataSize()));
			response.setHeader("Content-Disposition", "filename=" + fileName + ".png");

			// print data to stream
			response.getOutputStream().write(barcode.getData());
		} catch (Exception e) {

			e.printStackTrace();

			baseRequest.setHandled(true);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setHeader("Server", "BarcodeAPI.org");

			// add character set
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Accept-Charset", "utf-8");

			// add headers describing request
			response.setHeader("X-RequestTime", Long.toString(requestTime));
			response.setHeader("X-CodeServer", serverName);

			// print data to stream
			response.getOutputStream().println(e.getMessage());
		}
	}
}
