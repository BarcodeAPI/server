package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.gen.CodeType;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class CacheHandler extends AbstractHandler {

	public CacheHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get counters and increment stats hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("cache.dump.hits");

		// set response code
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		// loop each counter
		String output = "";

		// loop all barcode caches
		for (CodeType type : CodeType.values()) {
			for (String key : BarcodeCache.getCache(type).getKeys()) {
				output += type.toString() + ":" + key + "\n";
			}
		}

		// write to client
		response.getOutputStream().println(output);
	}
}
