package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.ServerRuntime;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoHandler extends RestHandler {

	public InfoHandler() {
		super(false, false);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		// print response to client
		JSONObject output = new JSONObject()//
				.put("uptime", ServerRuntime.getTimeRunning())//
				.put("hostname", ServerRuntime.getHostname())//
				.put("version", ServerRuntime.getVersion());
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(output.toString(4));
	}
}
