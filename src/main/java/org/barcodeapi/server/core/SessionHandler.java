package org.barcodeapi.server.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.session.SessionObject;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class SessionHandler extends AbstractHandler {

	private SessionCache sessionCache;

	public SessionHandler() {

		sessionCache = SessionCache.getInstance();
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get the users session
		SessionObject session = sessionCache.getSession(baseRequest);

		// get counters and increment session hits
		StatsCollector counters = StatsCollector.getInstance();
		counters.incrementCounter("session.dump.hits");

		// set response code
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		response.addHeader("Set-Cookie", "session=" + session.getKey() + ";");

		// print user session details
		response.getOutputStream().println(session.getDetails());
	}
}
