package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.session.SessionObject;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class StaticHandler extends ResourceHandler {
	
	private SessionCache sessions;

	public StaticHandler() {
		super();

		setResourceBase("resources");
		
		sessions = SessionCache.getInstance();
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		// get the users session
		SessionObject session = sessions.getSession(baseRequest);
		
		// set session cookie
		response.addHeader("Set-Cookie", "session=" + session.getKey() + ";");

		// hit counter
		StatsCollector.getInstance().incrementCounter("static.hits");

		// default handler
		super.handle(target, baseRequest, request, response);
	}
}
