package org.barcodeapi.server.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class CacheHandler extends AbstractHandler {

	public CacheHandler() {

	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get counters and increment stats hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("cache.hits");

		// set response code
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		// loop each counter
		for (String key : BarcodeCache.getInstance().getCacheKeys()) {

			response.getOutputStream().println(key);
		}
	}
}
