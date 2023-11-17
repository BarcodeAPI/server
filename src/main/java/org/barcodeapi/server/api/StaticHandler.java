package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class StaticHandler extends RestHandler {

	private ResourceHandler resources;

	public StaticHandler(Server server) throws Exception {
		super(false, false);

		resources = new ResourceHandler();
		resources.setServer(server);
		resources.setResourceBase("resources");
		resources.setRedirectWelcome(true);
		resources.setWelcomeFiles(new String[] { "index.html" });
		resources.start();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		// call through to resources
		baseRequest.setHandled(false);
		resources.handle(target, baseRequest, request, response);

		// send non resources to api
		if (!baseRequest.isHandled()) {
			response.sendRedirect("/api/auto" + request.getPathInfo());
		}
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws Exception {
	}
}
