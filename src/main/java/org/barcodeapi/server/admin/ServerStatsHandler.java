package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * ServerStatsHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ServerStatsHandler extends RestHandler {

	public ServerStatsHandler() {
		super(true);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Determine which stats cache to use
		String cache = c.getRequest().getParameter("cache");
		cache = (cache != null) ? cache : "default";

		// Get the details from the requested instance
		JSONObject stats = LibMetrics.instance(cache).getDetails();

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println(stats.toString());
	}
}
