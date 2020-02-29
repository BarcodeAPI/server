package org.barcodeapi.server.session;

import org.barcodeapi.core.cache.ObjectCache;

public class SessionCache extends ObjectCache<String, SessionObject> {

	private static SessionCache sessionCache;

	/**
	 * Initialize session cache.
	 */
	public SessionCache() {
		super("session");
	}

	public SessionObject createNewSession() {

		// create the new session object
		SessionObject session = new SessionObject();

		// add session to the cache
		put(session.getKey(), session);

		// return the session
		return session;
	}

	public static synchronized SessionCache getInstance() {

		if (sessionCache == null) {

			sessionCache = new SessionCache();
		}
		return sessionCache;
	}

	@Override
	public void onExpire(String key, SessionObject value) {
		// TODO Auto-generated method stub

	}
}
