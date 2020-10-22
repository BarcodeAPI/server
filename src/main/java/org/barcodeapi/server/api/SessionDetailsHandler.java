package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;

public class SessionDetailsHandler extends RestHandler {

	public SessionDetailsHandler() {
		super();
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		// print user session details
		response.getOutputStream()//
				.println(getSession(request).getDetails());
	}
}
