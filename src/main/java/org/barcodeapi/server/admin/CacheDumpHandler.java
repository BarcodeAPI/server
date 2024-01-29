package org.barcodeapi.server.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CacheDumpHandler extends RestHandler {

	public CacheDumpHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		// loop all caches
		JSONObject types = new JSONObject();
		for (String type : CodeTypes.inst().getTypes()) {

			// loop all cached objects
			JSONArray entries = new JSONArray();
			for (Map.Entry<String, CachedObject> entry : BarcodeCache.getCache(type).getRawCache().entrySet()) {

				entries.put(new JSONObject()//
						.put("text", entry.getKey())//
						.put("hits", entry.getValue().getAccessCount()));
			}

			// add to master element
			types.put(type, entries);
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
