package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.Authlist;
import org.barcodeapi.server.core.Blacklist;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerReloadHandler extends RestHandler {

	public ServerReloadHandler() {
		super(true);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		Authlist.reload();
		Blacklist.reload();

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "config reloaded");
		response.getOutputStream().println(output.toString(4));
	}
}
