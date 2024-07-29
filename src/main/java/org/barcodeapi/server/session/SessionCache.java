package org.barcodeapi.server.session;

import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.ObjectCache;

/**
 * SessionCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionCache {

	public static ObjectCache getCache() {
		return ObjectCache.getCache("sessions");
	}

	public static CachedSession getSession(String key) {

		CachedObject o = getCache().get(key);
		if (o == null) {
			return null;
		}

		return (CachedSession) o;
	}

	public static CachedSession createNewSession() {

		// create the new session object
		CachedSession session = new CachedSession();

		// add session to the cache
		getCache().put(session.getKey(), session);

		// return the session
		return session;
	}
}
