package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedSession;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SessionStatusHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class SessionStatusHandler extends RestHandler {

	public SessionStatusHandler() {
		super(
				// Authentication required
				true,
				// Do not use client rate limit
				false,
				// Do not create new session
				false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		String key = c.getRequest().getParameter("key");

		// Check the requested session exists
		if (!ObjectCache.getCache(ObjectCache.CACHE_SESSIONS).has(key)) {

			// Print failure to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject()//
					.put("code", 400)//
					.put("message", "session not found")//
			).toString());
			return;
		}

		CachedSession session = //
				(CachedSession) ObjectCache//
						.getCache(ObjectCache.CACHE_SESSIONS).get(key);

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(session.asJSON().toString(4));
	}
}
