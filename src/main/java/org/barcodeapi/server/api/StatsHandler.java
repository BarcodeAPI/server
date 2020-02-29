package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;

public class StatsHandler extends RestHandler {

	private final long timeStart;

	public StatsHandler() {

		timeStart = System.currentTimeMillis();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		// get counters and increment stats hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("stats.dump.hits");

		// current up-time
		counters.setCounter("system.uptime", (double) (System.currentTimeMillis() - timeStart));

		// calculate cache size
		double cacheSize = BarcodeCache.getInstance().getCacheSize();
		counters.setCounter("cache.size", cacheSize);

		// loop each counter
		String output = "";
		for (String key : counters.getCounters().keySet()) {

			// print key and value
			String value = String.format("%.0f", counters.getCounter(key));
			output += key + " : " + value + "\n";
		}

		response.getOutputStream().println(output);
	}
}
