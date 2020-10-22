package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONException;

public class CacheFlushHandler extends RestHandler {

	public CacheFlushHandler() {
		super(true);
	}

	@Override
	protected void onRequest(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		for (CodeType type : CodeType.values()) {
			BarcodeCache.getCache(type).clearCache();
		}
	}
}
