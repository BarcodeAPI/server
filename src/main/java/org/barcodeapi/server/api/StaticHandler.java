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

/**
 * StaticHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class StaticHandler extends RestHandler {

	private static final int CACHEsec = 604800;

	private ResourceHandler resources = new ResourceHandler() {
	};

	public StaticHandler(Server server) throws Exception {
		super();

		// Load the Jetty resource handler
		resources = new ResourceHandler();
		resources.setServer(server);
		resources.setResourceBase("resources");
		resources.setRedirectWelcome(true);
		resources.setWelcomeFiles(new String[] { "index.html" });
		resources.setCacheControl(("max-age=" + CACHEsec));
		resources.start();
	}

	@Override
	public void _impl(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// Calculate and set cache expiration time
		response.setDateHeader("expires", //
				(System.currentTimeMillis() + (CACHEsec * 1000)));

		// Call through to resources
		baseRequest.setHandled(false);
		resources.handle(target, baseRequest, request, response);

		// Send non resources to API
		if (!baseRequest.isHandled()) {
			response.sendRedirect("/api/auto" + request.getPathInfo());
		}
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws Exception {

		// Do Nothing.
		// This class overrides the lower level (handle) method to serve static files.
	}
}
