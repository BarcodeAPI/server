package org.barcodeapi.server.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.barcodeapi.server.core.CachedObject;

public class CachedSession extends CachedObject {

	private final String key;
	private final long timeCreated;
	private final ConcurrentHashMap<String, Integer> sessionRequests;

	public CachedSession() {
		this.setTimeout(6, TimeUnit.HOURS);

		this.key = UUID.randomUUID().toString();
		this.timeCreated = System.currentTimeMillis();
		this.sessionRequests = new ConcurrentHashMap<String, Integer>();
	}

	public String getKey() {

		return key;
	}

	public void hit(String data) {

		this.touch();
		if (!sessionRequests.containsKey(data)) {

			sessionRequests.put(data, 1);
			return;
		}

		sessionRequests.put(data, sessionRequests.get(data) + 1);
	}

	public String getDetails() {

		String details = "" + //
				"Key: " + getKey() + "\n" + //
				"Created: " + timeCreated + "\n" + //
				"Requests: " + sessionRequests.size() + "\n";

		for (String key : sessionRequests.keySet()) {

			details += String.format("%s :: %s\n", sessionRequests.get(key), key);
		}

		return details;
	}

	public Cookie getCookie() {

		Cookie cookie = new Cookie("session", getKey());
		cookie.setSecure(true);

		return cookie;
	}
}
