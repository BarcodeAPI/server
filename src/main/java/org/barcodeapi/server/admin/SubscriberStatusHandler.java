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
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SubscriberStatusHandler extends RestHandler {

	public SubscriberStatusHandler() {
		super(true, false, false);
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
