package org.barcodeapi.server.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LimiterListHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class LimiterListHandler extends RestHandler {

	public LimiterListHandler() {
		super(true, false, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		String filter = c.getRequest().getParameter("filter");
		boolean filtered = (filter != null);
		boolean filterReputation = (filtered && filter.equals("abusers"));
		boolean filterTokenLimit = (filtered && filter.equals("limited"));

		// List limiters
		JSONObject byKey = new JSONObject();
		ObjectCache keyCache = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);
		for (Map.Entry<String, CachedObject> entry : keyCache.raw().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();

			// Add all to map if no filters applied
			if (!filtered) {
				byKey.put(limiter.getCaller(), limiter.getTokens().getTotalSpend());
				continue;
			}

			// Conditionally filter based on low reputation status
			if (filterReputation && limiter.getReputation().isAbuser()) {
				byKey.put(limiter.getCaller(), limiter.getReputation().value());
				continue;
			}

			// Conditionally filter based on low tokens count
			if (filterTokenLimit && limiter.getTokens().isLowBalance()) {
				byKey.put(limiter.getCaller(), limiter.getTokens().getCount());
			}
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("limiters", byKey)//
		).toString());
	}
}
