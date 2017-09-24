package org.barcodeapi.server;

import java.io.IOException;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.krysalis.barcode4j.BarcodeException;
import org.xml.sax.SAXException;

public class ErrorPageErrorHandler extends ErrorHandler {

	final int dpi = 100;

	public ErrorPageErrorHandler() throws ConfigurationException, SAXException, IOException, BarcodeException {

	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String data = target.substring(1, target.length());

		response.addHeader("Server", "barcodeapi.org");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getOutputStream().println("Error [ " + data + " ]");
	}
}
