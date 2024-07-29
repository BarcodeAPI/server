package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.CodeTypes;
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
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		JSONObject counts = new JSONObject();
		for (String type : CodeTypes.inst().getTypes()) {

			double count = BarcodeCache.getCache(type).clearCache();
			counts.put(type, count);
		}

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "caches flushed")//
				.put("counts", counts);
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(output.toString(4));
	}
}
