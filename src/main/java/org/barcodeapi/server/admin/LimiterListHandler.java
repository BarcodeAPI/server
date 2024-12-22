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
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterListHandler extends RestHandler {

	public LimiterListHandler() {
		super(true, false, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// List by IP
		JSONObject byIp = new JSONObject();
		ObjectCache ipCache = ObjectCache.getCache(ObjectCache.CACHE_IP);
		for (Map.Entry<String, CachedObject> entry : ipCache.raw().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();
			byIp.put(limiter.getCaller(), limiter.getTokenCount());
		}

		// List by key
		JSONObject byKey = new JSONObject();
		ObjectCache keyCache = ObjectCache.getCache(ObjectCache.CACHE_KEY);
		for (Map.Entry<String, CachedObject> entry : keyCache.raw().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();
			byKey.put(limiter.getCaller(), limiter.getTokenCount());
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("ips", byIp)//
				.put("keys", byKey)//
		).toString());
	}
}
