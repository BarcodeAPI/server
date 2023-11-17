package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.session.SessionCache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionListHandler extends RestHandler {

	public SessionListHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response)
			throws JSONException, IOException {

		JSONArray sessions = new JSONArray();
		for (String key : SessionCache.getCache().getRawCache().keySet()) {
			sessions.put(key);
		}

		// print response to client
		JSONObject output = new JSONObject()//
				.put("sessions", sessions)//
				.put("count", sessions.length());
		response.getOutputStream().println(output.toString(4));
	}
}
