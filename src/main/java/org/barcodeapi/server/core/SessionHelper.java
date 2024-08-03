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

	public static CachedSession getSession(String key) {

		CachedObject o = ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).get(key);

		return (o == null) ? null : (CachedSession) o;
	}

	public static CachedSession createSession() {

		// create the new session object
		CachedSession session = new CachedSession();

		// add session to the cache
		ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).put(//
						session.getKey(), session);

		// return the session
		return session;
	}

	public static CachedSession getSession(Request request) {

		// get existing user session
		CachedSession session = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("session")) {
					session = getSession(cookie.getValue());
				}
			}
		}

		// new session if none existing
		if (session == null) {
			session = createSession();
		}

		return session;
	}

	public static boolean validateAdmin(Request request) {

		// false if no authentication
		String auth = request.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Basic")) {
			return false;
		}

		String authString = auth.substring(6);
		String decode = new String(Base64.getDecoder().decode(authString));
		String[] unpw = decode.split(":");

		String uName = unpw[0];
		String passHash = LibExtrasHashes.sumSHA256(unpw[1].getBytes());

		JSONObject admins = AppConfig.get().getJSONObject("admins");
		return admins.getString(uName).equals(passHash);
	}
}
