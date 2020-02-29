package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;

public class SessionHandler extends RestHandler {

	public SessionHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		// get counters and increment session hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("session.dump.hits");

		// print user session details
		// FIXME session
		// response.getOutputStream().println(session.getDetails());
	}
}
