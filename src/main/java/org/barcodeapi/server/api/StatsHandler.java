package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;

public class StatsHandler extends RestHandler {

	public StatsHandler() {
		super();
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		// print response to client
		response.getOutputStream().println(getStats().getDetails().toString(4));
	}
}
