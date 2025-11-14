package org.barcodeapi.server.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.CachedShare;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasStreams;
import com.mclarkdev.tools.liblog.LibLog;

/**
 * ShareHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ShareHandler extends RestHandler {

	private static final ObjectCache shareCache = //
			ObjectCache.getCache(ObjectCache.CACHE_SHARE);

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
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 405)//
					.put("message", "method not allowed")//
			).toString());
			return;
		}
	}

	/**
	 * Handle a GET request to the /share/ handler.
	 * 
	 * Returns the share associated with the key.
	 * 
	 * @param c request context
	 * @param r response context
	 * @throws IOException processing failure
	 */
	private void doShareGet(RequestContext c, HttpServletResponse r) throws IOException {

		String key = c.getRequest().getParameter("key");

		CachedObject obj = shareCache.get(key);

		if (obj == null) {
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "share not found")//
			).toString());
			return;
		}

		// Load the share data
		String shareData = ((CachedShare) obj).encodeJSON();

		// Print share data to the client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(shareData);
	}

	/**
	 * Handle a POST request to the /share/ handler.
	 * 
	 * Creates a share with the given set of requests.
	 * 
	 * Returns the share key associated with the new share.
	 * 
	 * @param c request context
	 * @param r response context
	 * @throws IOException processing failure
	 */
	private void doSharePost(RequestContext c, HttpServletResponse r) throws IOException {

		// Read input stream from the client
		String input = LibExtrasStreams//
				.readStream(c.getRequest().getInputStream(), 1024);

		JSONArray requests;
		try {

			// Parse user input as JSON array
			requests = new JSONArray(input);
		} catch (JSONException e) {

			// Print failure to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "invalid share data")//
			).toString());
			return;
		}

		// Loop each user request string
		List<BarcodeRequest> requestObjects = new ArrayList<>();
		for (int index = 0; index < requests.length(); index++) {
			String uri = requests.getString(index);

			try {

				// Parse request string as BarcodeRequest, add to map
				requestObjects.add(BarcodeRequest.fromURI(uri));
			} catch (Exception e) {

				// Log failure but continue
				LibLog._clogF("E0539", uri, e.getMessage());
			}
		}

		// Create new share from the data
		CachedShare share = new CachedShare(requestObjects);

		// Add the share into the cache
		shareCache.put(share.getHash(), share);

		// Print share key to the client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(share.getHash());
	}
}
