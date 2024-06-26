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
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		JSONObject byIp = new JSONObject();
		for (Map.Entry<String, CachedObject> entry : LimiterCache.getIpCache().getRawCache().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();
			byIp.put(limiter.getCaller(), limiter.numTokens());
		}

		JSONObject byKey = new JSONObject();
		for (Map.Entry<String, CachedObject> entry : LimiterCache.getKeyCache().getRawCache().entrySet()) {
			CachedLimiter limiter = (CachedLimiter) entry.getValue();
			byKey.put(limiter.getCaller(), limiter.numTokens());
		}

		// print response to client
		JSONObject output = new JSONObject()//
				.put("ips", byIp)//
				.put("keys", byKey);
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(output.toString(4));
	}
}
