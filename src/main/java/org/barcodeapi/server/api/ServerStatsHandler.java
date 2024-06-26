package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;

/**
 * ServerStatsHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ServerStatsHandler extends RestHandler {

	public ServerStatsHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		// print response to client
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(getStats().getDetails().toString(4));
	}
}
