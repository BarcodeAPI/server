package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONException;

public class CacheDumpHandler extends RestHandler {

	public CacheDumpHandler() {
		super(true);
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		// loop all caches
		String output = "";
		for (CodeType type : CodeType.values()) {
			for (String key : BarcodeCache.getCache(type).getKeys()) {
				output += type.toString() + ":" + key + "\n";
			}
		}

		// write to client
		response.getOutputStream().println(output);
	}
}
