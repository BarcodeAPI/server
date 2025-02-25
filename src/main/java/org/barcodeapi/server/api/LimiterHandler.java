package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONObject;

/**
 * SessionDetailsHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterHandler extends RestHandler {

	public LimiterHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println(//
				c.getLimiter().asJSON().toString(4));
	}
}
