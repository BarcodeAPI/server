package org.barcodeapi.server.core;

import java.util.Base64;

import javax.servlet.http.Cookie;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.CachedSession;
import org.barcodeapi.server.cache.ObjectCache;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

/**
 * SessionHelper.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionHelper {

	/**
	 * Returns a user session object for the provided session key.
	 * 
	 * @param key the session key
	 * @return the user session object
	 */
	public static CachedSession getSession(String key) {

		CachedObject o = ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).get(key);

		return (o == null) ? null : (CachedSession) o;
	}

	/**
	 * Creates a new user session object.
	 * 
	 * @return the user session object
	 */
	public static CachedSession createSession() {

		// Create the new session object
		CachedSession session = new CachedSession();

		// Add session to the cache
		ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).put(//
						session.getKey(), session);

		// Return the session
		return session;
	}

	/**
	 * Lookup a user session object for a given HTTP request.
	 * 
	 * @param request the raw request
	 * @return the user session object
	 */
	public static CachedSession getSession(Request request) {

		// Get existing user session
		CachedSession session = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("session")) {
					session = getSession(cookie.getValue());
				}
			}
		}

		return session;
	}

	/**
	 * Validate a user based on the provided Authentication string.
	 * 
	 * @param request
	 * @return
	 */
	public static boolean validateAdmin(Request request) {

		// False if no authentication
		String auth = request.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Basic")) {
			return false;
		}

		// Parse authentication string
		String authString = auth.substring(6);
		String decode = new String(Base64.getDecoder().decode(authString));
		String[] unpw = decode.split(":");

		String uName = unpw[0];
		String passHash = LibExtrasHashes.sumSHA256(unpw[1].getBytes());

		// Check if login exists in user file
		JSONObject admins = AppConfig.get().getJSONObject("admins");
		return admins.getString(uName).equals(passHash);
	}
}
