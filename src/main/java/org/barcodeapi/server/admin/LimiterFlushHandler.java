package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.LimiterCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LimiterFlushHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class LimiterFlushHandler extends RestHandler {

	public LimiterFlushHandler() {
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

		// Check & flush caches
		String caller = c.getRequest().getParameter("caller");
		boolean flushed = LimiterCache.flushLimiter(caller);

		if (!flushed) {

			// Print failure to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject()//
					.put("code", 400)//
					.put("message", "limiter not found")//
			).toString());
			return;
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("message", "limiter flushed")//
		).toString());
	}
}
