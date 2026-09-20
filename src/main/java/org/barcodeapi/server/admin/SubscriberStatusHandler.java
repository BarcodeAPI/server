package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SubscriberReloadHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class SubscriberStatusHandler extends RestHandler {

	public SubscriberStatusHandler() {
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

		if (c.getSubscriber() == null) {
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "not subscribed")//
			).toString());
		}

		// Load subscriber details
		JSONObject subscriberDetails = //
				c.getSubscriber().asJSON();

		// Attach list of associations
		subscriberDetails.put("associations", //
				c.getSubscriber().getAssociations());

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(//
				subscriberDetails.toString(4));
	}
}
