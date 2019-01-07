package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class StatsHandler extends AbstractHandler {

	private final long timeStart;

	public StatsHandler() {

		timeStart = System.currentTimeMillis();
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get counters and increment stats hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("stats.dump.hits");

		// current up-time
		counters.setCounter("system.uptime", (double) (System.currentTimeMillis() - timeStart));

		// calculate cache size
		double cacheSize = BarcodeCache.getInstance().getCacheSize();
		counters.setCounter("cache.size", cacheSize);

		// number of total sessions
		SessionCache sessions = SessionCache.getInstance();
		counters.setCounter("sessions.created", sessions.getTotalSessionCount());
		counters.setCounter("sessions.active", sessions.getActiveSessionCount());

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
