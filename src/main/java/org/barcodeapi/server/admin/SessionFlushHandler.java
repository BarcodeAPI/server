package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.session.SessionCache;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionFlushHandler extends RestHandler {

	public SessionFlushHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		double count = SessionCache.getCache().clearCache();

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "sessions flushed")//
				.put("count", count);
		response.getOutputStream().println(output.toString(4));
	}
}
