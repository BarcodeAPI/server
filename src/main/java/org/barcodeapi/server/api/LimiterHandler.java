package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.core.Tokens;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class LimiterHandler extends RestHandler {

	public LimiterHandler() {
		super(
				// Authentication not required
				false,
				// Do not use client rate limit
				false,
				// Do not create new session
				false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		switch (c.getMethod()) {
		case "GET":
			doGET(c, r);
			break;

		case "DELETE":
			doDELETE(c, r);
			break;
		}
	}

	protected void doGET(RequestContext c, HttpServletResponse r) throws IOException {

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(//
				c.getLimiter().asJSON().toString(4));
	}

	protected void doDELETE(RequestContext c, HttpServletResponse r) throws IOException {

		// Fail if user does not have a session
		if (!c.hasSession()) {
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "no session in request")//
			).toString());
			return;
		}

		// Log the reset request
		LibLog._logF("Limiter reset request: %s", c.getLimiter().getCaller());

		// Reset the limiter balance to full
		Tokens userTokens = c.getLimiter().getTokens();
		userTokens.refund(userTokens.getLimit());

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println((new JSONObject() //
				.put("code", 200)//
				.put("message", "token balance reset")//
		).toString());
	}
}
