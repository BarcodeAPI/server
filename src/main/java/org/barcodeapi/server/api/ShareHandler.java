package org.barcodeapi.server.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.CachedShare;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ShareHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ShareHandler extends RestHandler {

	public ShareHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws IOException {

		switch (c.getMethod()) {
		case "GET":
			doShareGet(c, r);
			return;

		case "POST":
			doSharePost(c, r);
			return;

		default:
			// Print error to client
			r.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 405)//
					.put("message", "method not allowed")//
			).toString());
			return;
		}
	}

	private void doShareGet(RequestContext c, HttpServletResponse r) throws IOException {

		String key = c.getRequest().getParameter("key");

		CachedObject obj = ObjectCache.getCache(ObjectCache.CACHE_SHARE).get(key);

		if (obj == null) {
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "share not found")//
			).toString());
			return;
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println(((CachedShare) obj).getData());
	}

	private void doSharePost(RequestContext c, HttpServletResponse r) throws IOException {

		BufferedReader reader = new BufferedReader(//
				new InputStreamReader(c.getRequest().getInputStream()));

		String line, input = "";
		while ((line = reader.readLine()) != null) {
			input += line;
		}

		JSONArray requests = new JSONArray(input);

		CachedShare share = new CachedShare(requests);

		ObjectCache.getCache(ObjectCache.CACHE_SHARE).put(share.getHash(), share);

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.getOutputStream().println(share.getHash());
	}
}
