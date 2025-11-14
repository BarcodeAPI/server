package org.barcodeapi.server.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CacheDumpHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CacheDumpHandler extends RestHandler {

	public CacheDumpHandler() {
		super(true, false, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Loop all caches
		JSONObject types = new JSONObject();
		for (String type : CodeTypes.inst().getTypes()) {

			// Loop all cached objects
			JSONArray entries = new JSONArray();
			ObjectCache cache = ObjectCache.getCache(type);
			for (Map.Entry<String, CachedObject> entry : cache.raw().entrySet()) {

				CachedObject obj = entry.getValue();

				entries.put(new JSONObject()//
						.put("text", entry.getKey())//
						.put("hits", obj.getAccessCount())//
						.put("created", obj.getTimeCreated())//
						.put("expires", obj.getTimeExpires())//
						.put("shortLived", obj.isShortLived()));
			}

			// Add to master element
			types.put(type, entries);
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.setHeader("Content-Disposition", "attachment, filename=cache.json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("cache", types)//
		).toString());
	}
}
