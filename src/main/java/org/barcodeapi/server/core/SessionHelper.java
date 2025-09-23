package org.barcodeapi.server.core;

import java.util.Base64;

import javax.servlet.http.Cookie;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
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

	private static final JSONObject _admins = Config.get(Cfg.Admins);

	/**
	 * Returns a user session object for the provided session key.
	 * 
	 * @param key the session key
	 * @return the user session object
	 */
	public static CachedSession getSession(String key) {

		CachedObject o = ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).get(key);

		return ((o != null) ? ((CachedSession) o) : null);
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
	 * Delete a user session from the cache.
	 * 
	 * @param sid the session id
	 * @return session removed
	 */
	public static boolean deleteSession(String sid) {

		CachedObject removed = ObjectCache.getCache(//
				ObjectCache.CACHE_SESSIONS).remove(sid);
		return (removed != null);
	}

	/**
	 * Lookup a user session object for a given HTTP request.
	 * 
	 * @param request the raw request
	 * @return the user session object
	 */
	public static CachedSession getSession(Request request) {

		// Get existing user session
		if (request.getCookies() == null) {
			return null;
		}

		String sessionKey = null;
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("session")) {
				sessionKey = cookie.getValue();
				break;
			}
		}

		// Lookup session if key found, else return null
		return (sessionKey != null) ? getSession(sessionKey) : null;
	}

	/**
	 * Validate a user based on the provided Authentication string.
	 * 
	 * @param request
	 * @return
	 */
	public static String validateUser(String basicAuth) {

		// False if no authentication string
		if (basicAuth == null) {
			return null;
		}

		String decoded;
		try {

			// Decode as Base64
			decoded = new String(//
					Base64.getDecoder().decode(basicAuth));
		} catch (IllegalArgumentException e) {

			// Fail if unable to decode
			return null;
		}

		// Determine where to split
		int split = decoded.indexOf(':');
		if (split < 1) {

			// Fail if not user:pass
			return null;
		}

		// Split BasicAuth on [:]
		String uName = decoded.substring(0, split);
		String pWord = decoded.substring(split + 1);

		// Calculate the expected password hash
		String passHash = LibExtrasHashes.sumSHA256(pWord.getBytes());

		// Check if login exists in app config and return
		return (passHash.equals(_admins.optString(uName)) ? uName : null);
	}
}
