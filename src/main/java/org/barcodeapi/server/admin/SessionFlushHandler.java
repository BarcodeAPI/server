package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.session.SessionCache;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionFlushHandler extends RestHandler {

	public SessionFlushHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		double count = SessionCache.getCache().clearCache();

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "sessions flushed")//
				.put("count", count);
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(output.toString(4));
	}
}
