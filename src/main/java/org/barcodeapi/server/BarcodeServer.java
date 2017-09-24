package org.barcodeapi.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class BarcodeServer extends AbstractHandler {

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		response.addHeader("Server", "barcodeapi.org");

		String[] requestParts = target.split("\\/");

		if (requestParts.length == 0) {

			// TODO display home page
		}

		switch (requestParts[1]) {

		case "128":
		case "matrix":
		case "qr":
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			break;

		default:
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Barcode type unavailalbe.");
			baseRequest.setHandled(false);
			break;
		}

	}
}