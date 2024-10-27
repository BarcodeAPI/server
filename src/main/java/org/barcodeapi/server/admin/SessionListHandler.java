package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SessionListHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionListHandler extends RestHandler {

	public SessionListHandler() {
		super(true, false, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Loop all sessions
		JSONArray sessions = new JSONArray();
		for (String key : ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).raw().keySet()) {
			sessions.put(key);
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("sessions", sessions)//
				.put("count", sessions.length())//
		).toString());
	}
}
