package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SessionFlushHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class SessionFlushHandler extends RestHandler {

	public SessionFlushHandler() {
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

		// Clear cache and get count
		double count = ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).clearCache();

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("message", "sessions flushed")//
				.put("count", count)//
		).toString());
	}
}
