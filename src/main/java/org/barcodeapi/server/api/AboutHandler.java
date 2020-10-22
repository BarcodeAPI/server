package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.ServerRuntime;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutHandler extends RestHandler {

	public AboutHandler() {
		super();
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		response.getOutputStream().println((new JSONObject()//
				.put("runtimeId", ServerRuntime.getRuntimeID())//
				.put("uptime", "---")//
				.put("admin", "---")//
				.put("hostname", "---")//
		).toString(4));
	}
}
