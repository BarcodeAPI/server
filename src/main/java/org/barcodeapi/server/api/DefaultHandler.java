package org.barcodeapi.server.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

public class DefaultHandler extends ErrorHandler {

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {

		// hit counter
		StatsCollector.getInstance().incrementCounter("error.redirect");

		try {

			response.sendRedirect("/api/auto" + request.getPathInfo());
		} catch (Exception e) {

			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
