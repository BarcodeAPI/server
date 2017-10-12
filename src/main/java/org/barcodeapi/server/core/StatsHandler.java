package org.barcodeapi.server.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class StatsHandler extends AbstractHandler {

	public StatsHandler() {

	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get counters and increment stats hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("stats.hits");

		// calculate cache size
		double cacheSize = BarcodeCache.getInstance().getCacheSize();
		counters.setCounter("cache.size", cacheSize);

		// set response code
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		// loop each counter
		for (String key : counters.getCounters().keySet()) {

			// print key and value
			String value = String.format("%.0f", counters.getCounter(key));
			response.getOutputStream().println(key + " : " + value);
		}
	}
}
