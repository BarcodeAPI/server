package org.barcodeapi.server.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class StaticHandler extends ResourceHandler {

	public StaticHandler() {
		super();

		setResourceBase("resources");
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// hit counter
		StatsCollector.getInstance().incrementCounter("static.hits");

		// default handler
		super.handle(target, baseRequest, request, response);
	}
}
