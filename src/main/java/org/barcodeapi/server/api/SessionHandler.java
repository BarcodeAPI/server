package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.core.SessionHelper;
import org.json.JSONObject;

/**
 * SessionDetailsHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionHandler extends RestHandler {

	public SessionHandler() {
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

		if (!c.hasSession()) {
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "no session in request")//
			).toString());
			return;
		}

		switch (c.getMethod()) {
		case "GET":
			doGET(c, r);
			break;

		case "DELETE":
			doDELETE(c, r);
			break;

		default:
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			break;
		}
	}

	protected void doGET(RequestContext c, HttpServletResponse r) throws IOException {

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println(//
				c.getSession().asJSON().toString(4));
	}

	protected void doDELETE(RequestContext c, HttpServletResponse r) throws IOException {

		// Attempt session deletion
		boolean deleted = SessionHelper.deleteSession(c.getSession().getKey());

		// Print response to client
		r.setStatus((deleted) ? //
				HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
	}
}
