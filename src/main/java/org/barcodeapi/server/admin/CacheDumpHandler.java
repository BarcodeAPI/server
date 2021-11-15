package org.barcodeapi.server.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CacheDumpHandler extends RestHandler {

	public CacheDumpHandler() {
		super(true);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		// loop all caches
		JSONObject types = new JSONObject();
		for (CodeType type : CodeType.values()) {

			JSONArray entries = new JSONArray();
			types.put(type.toString(), entries);

			// loop all cached objects
			for (Map.Entry<String, CachedObject> entry : BarcodeCache.getCache(type).getRawCache().entrySet()) {

				entries.put(new JSONObject()//
						.put("text", entry.getKey())//
						.put("hits", entry.getValue().getAccessCount()));
			}
		}

		// download the results
		response.setHeader("Content-Type", "application/json");
		response.setHeader("Content-Disposition", "attachment, filename=cache.json");

		// print response to client
		JSONObject output = new JSONObject()//
				.put("cache", types);
		response.getOutputStream().println(output.toString(4));
	}
}
