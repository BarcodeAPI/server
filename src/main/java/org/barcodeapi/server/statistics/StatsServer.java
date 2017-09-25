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

		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		for (String key : StatsCollector.getInstance().getCounters().keySet()) {

			response.getOutputStream().println(key + " : " + StatsCollector.getInstance().getCounter(key));
		}
	}
}
