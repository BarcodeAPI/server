package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;

public class StatsDumpHandler extends RestHandler {

	public StatsDumpHandler() {
		super();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		response.getOutputStream().println(getStats().dumpJSON().toString());
	}
}
