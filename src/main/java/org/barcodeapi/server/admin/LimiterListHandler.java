package org.barcodeapi.server.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.limits.CachedLimiter;
import org.barcodeapi.server.limits.LimiterCache;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LimiterListHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterListHandler extends RestHandler {

	public LimiterListHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// List by IP
		JSONObject byIp = new JSONObject();
		for (Map.Entry<String, CachedObject> entry : LimiterCache.getIpCache().getRawCache().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();
			byIp.put(limiter.getCaller(), limiter.numTokens());
		}

		// List by key
		JSONObject byKey = new JSONObject();
		for (Map.Entry<String, CachedObject> entry : LimiterCache.getKeyCache().getRawCache().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();
			byKey.put(limiter.getCaller(), limiter.numTokens());
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
