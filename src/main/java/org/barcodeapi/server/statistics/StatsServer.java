package org.barcodeapi.server.statistics;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class StatsServer extends AbstractHandler {

	public StatsServer() {

	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		StatsCollector.getInstance().incrementCounter("stats.hits");

		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		StatsCollector counters = StatsCollector.getInstance();
		for (String key : counters.getCounters().keySet()) {

			response.getOutputStream().println(key + " : " + counters.getCounter(key));
		}
	}
}
