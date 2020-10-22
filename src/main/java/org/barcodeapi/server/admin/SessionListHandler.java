package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.session.SessionCache;
import org.json.JSONException;

public class SessionListHandler extends RestHandler {

	public SessionListHandler() {
		super(true);
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		String output = "";
		for (String key : SessionCache.getCache().getKeys()) {
			output += key + "\n";
		}

		// write to client
		response.getOutputStream().println(output);
	}
}
