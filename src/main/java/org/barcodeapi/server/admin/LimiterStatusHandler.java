package org.barcodeapi.server.admin;

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
public class LimiterStatusHandler extends RestHandler {

	public LimiterStatusHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(//
				c.getLimiter().asJSON().toString(4));
	}
}
