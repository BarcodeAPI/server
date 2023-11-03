package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONException;
import org.json.JSONObject;

public class CacheFlushHandler extends RestHandler {

	public CacheFlushHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		JSONObject counts = new JSONObject();
		for (CodeType type : CodeType.values()) {

			double count = BarcodeCache.getCache(type).clearCache();
			counts.put(type.toString(), count);
		}

		// print response to client
		JSONObject output = new JSONObject()//
				.put("message", "caches flushed")//
				.put("counts", counts);
		response.getOutputStream().println(output.toString(4));
	}
}
