package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.LimiterCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONObject;

/**
 * LimiterStatusHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class LimiterStatusHandler extends RestHandler {

	public LimiterStatusHandler() {
		super(
				// Authentication required
				true,
				// Do not use client rate limit
				false,
				// Do not create new session
				false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		String caller = c.getRequest().getParameter("caller");

		// Check the requested caller exists
		if (!LimiterCache.hasCaller(caller)) {

			// Print failure to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject()//
					.put("code", 400)//
					.put("message", "limiter not found")//
			).toString());
			return;
		}

		// Lookup the caller info
		CachedLimiter limiter = //
				LimiterCache.getLimiter(null, caller);

		// Fetch limiter details and history
		JSONObject limiterInfo = limiter.asJSON();
		limiterInfo.put("history", limiter.getHistroy());

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(//
				limiterInfo.toString(4));
	}
}
