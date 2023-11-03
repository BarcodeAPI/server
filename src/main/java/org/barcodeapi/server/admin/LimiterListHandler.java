package org.barcodeapi.server.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.limits.ClientLimiter;
import org.barcodeapi.server.limits.LimiterCache;
import org.json.JSONException;
import org.json.JSONObject;

public class LimiterListHandler extends RestHandler {

	public LimiterListHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		JSONObject byIp = new JSONObject();
		for (Map.Entry<String, CachedObject> entry : LimiterCache.getIpCache().getRawCache().entrySet()) {
			ClientLimiter limiter = (ClientLimiter) entry.getValue();
			byIp.put(limiter.getCaller(), limiter.numTokens());
		}

		JSONObject byKey = new JSONObject();
		for (Map.Entry<String, CachedObject> entry : LimiterCache.getKeyCache().getRawCache().entrySet()) {
			ClientLimiter limiter = (ClientLimiter) entry.getValue();
			byKey.put(limiter.getCaller(), limiter.numTokens());
		}

		// print response to client
		JSONObject output = new JSONObject()//
				.put("ips", byIp)//
				.put("keys", byKey);
		response.getOutputStream().println(output.toString(4));
	}
}
