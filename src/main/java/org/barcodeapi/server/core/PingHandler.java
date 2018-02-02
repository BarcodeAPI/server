package org.barcodeapi.server.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class PingHandler extends AbstractHandler {

	public PingHandler() {

	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get counters and increment ping hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("ping.hits");

		// set response code
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
	}
}
