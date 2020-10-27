package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.Authlist;
import org.barcodeapi.server.core.Blacklist;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;

public class ConfigReloadHandler extends RestHandler {

	public ConfigReloadHandler() {
		super(true);
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		Authlist.reload();
		Blacklist.reload();
	}
}
