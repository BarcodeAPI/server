package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ServerReloadHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ServerReloadHandler extends RestHandler {

	public ServerReloadHandler() {
		super(true, false, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Reload config
		AppConfig.reload();

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("message", "config reloaded")//
		).toString());
	}
}
