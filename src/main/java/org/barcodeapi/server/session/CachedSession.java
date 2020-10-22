package org.barcodeapi.server.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.barcodeapi.server.core.CachedObject;

public class CachedSession extends CachedObject {

	private final String key;
	private final ConcurrentHashMap<String, Integer> sessionRequests;

	public CachedSession() {
		this.setTimeout(6, TimeUnit.HOURS);

		this.key = UUID.randomUUID().toString();
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

		String requests = "";
		int requestCount = 0;
		for (String key : sessionRequests.keySet()) {

			int count = sessionRequests.get(key);
			requestCount += count;

			requests += String.format("%4d :: %s\n", count, key);
		}

		return "\n" + //
				" Key: " + getKey() + "\n" + //
				" Created: " + getTimeCreated() + "\n" + //
				" Last Seen: " + getTimeLastSeen() + "\n" + //
				" Expiration: " + getTimeExpires() + "\n" + //
				" Request Count: " + requestCount + "\n" + //
				"\n  --\n\n" + requests;
	}

	public Cookie getCookie() {

		return new Cookie("session", getKey());
	}
}
