package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.limits.LimiterCache;
import org.json.JSONException;
import org.json.JSONObject;

public class LimiterFlushHandler extends RestHandler {

	public LimiterFlushHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		String limiter = request.getParameter("limiter");

		if (limiter == null) {
			// print response to client
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			JSONObject output = new JSONObject()//
					.put("message", "limiter not set");
			response.getOutputStream().println(output.toString(4));
			return;
		}

		boolean flushed = false;

		if (LimiterCache.getKeyCache().has(limiter)) {
			LimiterCache.getKeyCache().remove(limiter);
			flushed = true;
		}

		if (LimiterCache.getIpCache().has(limiter)) {
			LimiterCache.getIpCache().remove(limiter);
			flushed = true;
		}

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "limiter flushed")//
				.put("flushed", flushed);
		response.getOutputStream().println(output.toString(4));
	}
}
