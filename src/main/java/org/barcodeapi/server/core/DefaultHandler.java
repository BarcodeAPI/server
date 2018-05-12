package org.barcodeapi.server.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

public class DefaultHandler extends ErrorHandler {

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {

		// hit counter
		StatsCollector.getInstance().incrementCounter("error.hits");

		try {

			response.sendRedirect("/api" + request.getPathInfo());
		} catch (Exception e) {

			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
