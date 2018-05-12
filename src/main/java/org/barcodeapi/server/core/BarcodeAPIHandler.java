package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.session.SessionObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class BarcodeAPIHandler extends AbstractHandler {

	private final String ERR = "/128/$$@E$$@R$$@R$$@O$$@R$$@";

	private String serverName;

	private SessionCache sessions;

	public BarcodeAPIHandler() {

		sessions = SessionCache.getInstance();

		try {

			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		// request is handled
		baseRequest.setHandled(true);

		// server name
		response.setHeader("Server", "BarcodeAPI.org");
		response.setHeader("X-CodeServer", serverName);

		// add character set
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Accept-Charset", "utf-8");

		CachedObject barcode;
		try {

			// generate user requested barcode
			barcode = BarcodeGenerator.requestBarcode(target);

			// response okay
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {

			String message = "Failed [ " + target + " ]" + //
					" reason [ " + e.toString() + " ]";
			System.out.println(System.nanoTime() + " : " + message);

			// generate error barcode
			barcode = BarcodeGenerator.requestBarcode(ERR);

			// set HTTP response code and add message to headers
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("X-Error-Message", message);
		}

		// additional properties
		String data = barcode.getProperties().getProperty("data");
		String nice = barcode.getProperties().getProperty("nice");
		String type = barcode.getProperties().getProperty("type");

		// FIXME parse data and cookies here
		// pass only session id to sessions
		// pass only session takes type and data

		// get the user session
		SessionObject session = sessions.getSession(baseRequest);

		// update user session
		session.onRender(data);

		// set session cookie
		response.addHeader("Set-Cookie", "session=" + session.getKey() + ";");

		// add cache headers
		response.setHeader("Cache-Control", "max-age=86400, public");

		// add content headers
		response.setHeader("Content-Type", "image/png");
		response.setHeader("Content-Length", Long.toString(barcode.getDataSize()));

		// file name when clicking save
		response.setHeader("Content-Disposition", "filename=" + nice + ".png");

		// barcode type
		response.setHeader("X-Barcode-Type", type);
		response.setHeader("X-Barcode-Content", nice);

		// print data to stream
		response.getOutputStream().write(barcode.getData());
	}
}
