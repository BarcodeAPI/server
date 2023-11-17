package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.AppConfig;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerReloadHandler extends RestHandler {

	public ServerReloadHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		AppConfig.reload();

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "config reloaded");
		response.getOutputStream().println(output.toString(4));
	}
}
