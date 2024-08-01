package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CacheFlushHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CacheFlushHandler extends RestHandler {

	public CacheFlushHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		JSONObject counts = new JSONObject();

		// Loop all configured types
		for (String type : CodeTypes.inst().getTypes()) {

			// Clear the cache and add count to map
			counts.put(type, ObjectCache.getCache(type).clearCache());
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("message", "caches flushed")//
				.put("counts", counts)//
		).toString());
	}
}
