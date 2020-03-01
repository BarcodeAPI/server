package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

public class RedirectHandler extends ErrorHandler {

	private final RestHandler restHandler;

	public RedirectHandler() {
		restHandler = new RestHandler();
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		try {

			// redirect to API
			restHandler.handle(target, baseRequest, request, response);
			response.sendRedirect("/api/auto" + request.getPathInfo());
		} catch (Exception e) {

			Log.out(LOG.ERROR, e.getMessage());
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
