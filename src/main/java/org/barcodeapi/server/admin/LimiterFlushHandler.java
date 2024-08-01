package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.limits.LimiterCache;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LimiterFlushHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterFlushHandler extends RestHandler {

	public LimiterFlushHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Lookup limiter info
		String limiter = c.getRequest().getParameter("limiter");

		// Fail if not found
		if (limiter == null) {

			// Print failure to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject()//
					.put("code", 400)//
					.put("message", "limiter not set")//
			).toString());
			return;
		}

		boolean flushed = false;

		// Check & flush Key cache
		if (LimiterCache.getKeyCache().has(limiter)) {
			LimiterCache.getKeyCache().remove(limiter);
			flushed = true;
		}

		// Check & flush IP cache
		if (LimiterCache.getIpCache().has(limiter)) {
			LimiterCache.getIpCache().remove(limiter);
			flushed = true;
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("message", "limiter flushed")//
				.put("flushed", flushed)//
		).toString());
	}
}
