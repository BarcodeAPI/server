package org.barcodeapi.server.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.core.CachedObject;

public class CachedSession implements CachedObject {

	private final String key;
	private final long timeCreated;
	private final ConcurrentHashMap<String, Integer> sessionRequests;

	private long hitCount = 0;
	private long timeTouched = 0;

	public CachedSession() {

		this.key = UUID.randomUUID().toString();
		this.timeCreated = System.currentTimeMillis();
		this.sessionRequests = new ConcurrentHashMap<String, Integer>();
	}

	public String getKey() {

		return key;
	}

	public long getLastTouchTime() {

		return timeTouched;
	}

	public void hit(String data) {

		hitCount++;
		timeTouched = System.currentTimeMillis();
		if (!sessionRequests.containsKey(data)) {

			sessionRequests.put(data, 1);
			return;
		}

		sessionRequests.put(data, sessionRequests.get(data) + 1);
	}

	public String getDetails() {

		String details = "" + //
				"Key: " + key + "\n" + //
				"Created: " + timeCreated + "\n" + //
				"Hits: " + hitCount + "\n\n";
		for (String key : sessionRequests.keySet()) {

			details += key + " : " + sessionRequests.get(key) + "\n";
		}

		return details;
	}
}
