package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LimiterFlushHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterFlushHandler extends RestHandler {

	public LimiterFlushHandler() {
		super(true, false, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		boolean flushed = false;
		String ip = c.getRequest().getParameter("ip");
		String key = c.getRequest().getParameter("key");

		// Check & flush caches
		flushed = flushed && flush(ObjectCache.CACHE_KEY, key);
		flushed = flushed && flush(ObjectCache.CACHE_IP, ip);

		if (!flushed) {

			// Print failure to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject()//
					.put("code", 400)//
					.put("message", "limiter not set (key|ip)")//
			).toString());
			return;
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("message", "limiter flushed")//
		).toString());
	}

	private boolean flush(String cache, String limiter) {
		ObjectCache objCache = ObjectCache.getCache(cache);
		if ((limiter != null) && objCache.has(limiter)) {
			objCache.remove(limiter);
			return true;
		}

		return false;
	}
}
