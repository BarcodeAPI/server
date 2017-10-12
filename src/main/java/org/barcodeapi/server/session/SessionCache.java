package org.barcodeapi.server.session;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.server.Request;

public class SessionCache {

	private static SessionCache sessionCache;

	private ConcurrentHashMap<String, SessionObject> cache;

	/**
	 * Initialize session cache.
	 */
	public SessionCache() {

		cache = new ConcurrentHashMap<String, SessionObject>();
	}

	public SessionObject getSession(Request request) {

		// check for cookie
		if (request.getCookies() != null) {

			// loop each cookie
			for (Cookie cookie : request.getCookies()) {

				// check for session cookie
				if (cookie.getName().equals("session")) {

					// check if cache has requested session
					if (cache.containsKey(cookie.getValue())) {

						// return session object
						return cache.get(cookie.getValue());
					}
				}
			}
		}

		// create and return new session object
		SessionObject session = new SessionObject();
		cache.put(session.getKey(), session);
		return session;
	}

	public static synchronized SessionCache getInstance() {

		if (sessionCache == null) {

			sessionCache = new SessionCache();
		}
		return sessionCache;
	}
}
