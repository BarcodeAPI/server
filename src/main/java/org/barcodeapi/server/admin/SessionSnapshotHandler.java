package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONObject;

/**
 * SessionListHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionSnapshotHandler extends RestHandler {

	public SessionSnapshotHandler() {
		super(true, false);
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		int count = 0;

		try {

			// Save the session cache
			count = ObjectCache.getCache(//
					ObjectCache.CACHE_SESSIONS).snapshot();

		} catch (IOException e) {

			// Snapshot failed
			r.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject()//
					.put("code", 500)//
					.put("error", e.getMessage())//
			).toString());
			return;
		}

		// Snapshot okay
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println((new JSONObject()//
				.put("code", 200)//
				.put("count", count)//
		).toString());
	}
}
