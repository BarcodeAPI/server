package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;

/**
 * SessionDetailsHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionDetailsHandler extends RestHandler {

	public SessionDetailsHandler() {
		super(false, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		// print response to client
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(ctx.getSession().getDetails().toString(4));
	}
}
