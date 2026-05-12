package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.SubscriberCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SubscriberReloadHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class SubscriberReloadHandler extends RestHandler {

	public SubscriberReloadHandler() {
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

		// Reload the subscribers list
		SubscriberCache.reload();

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("message", "users reloaded")//
		).toString());
	}
}
