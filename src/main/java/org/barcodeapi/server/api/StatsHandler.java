package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;

public class StatsHandler extends RestHandler {

	public StatsHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		// get counters and increment stats hits
		StatsCollector counters = StatsCollector.getInstance();

		// loop each counter
		JSONObject stats = new JSONObject();
		for (String key : counters.getCounters().keySet()) {

			// print key and value
			stats.put(key, counters.getCounter(key));
		}

		response.getOutputStream().println(stats.toString());
	}
}
