package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.session.SessionCache;
import org.eclipse.jetty.server.Request;

public class SessionDumpHandler extends RestHandler {

	public SessionDumpHandler() {
		super();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		String output = "";
		for (String key : SessionCache.getCache().getKeys()) {
			output += key + "\n";
		}

		// write to client
		response.getOutputStream().println(output);
	}
}
