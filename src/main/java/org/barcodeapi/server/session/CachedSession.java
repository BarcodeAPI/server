package org.barcodeapi.server.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.core.CachedObject;

public class CachedSession implements CachedObject {

	private final String key;
	private final long timeCreated;
	private final ConcurrentHashMap<String, Integer> sessionRequests;

	private long timeTouched = 0;

	public CachedSession() {

		this.key = UUID.randomUUID().toString();
		this.timeCreated = System.currentTimeMillis();
		this.sessionRequests = new ConcurrentHashMap<String, Integer>();
	}

	public String getKey() {

		return key;
	}

	public long getTimeCreated() {

		return timeCreated;
	}

	public long getTimeTouched() {

		return timeTouched;
	}

	public void hit(String data) {

		timeTouched = System.currentTimeMillis();
		if (!sessionRequests.containsKey(data)) {

			sessionRequests.put(data, 1);
			return;
		}

		sessionRequests.put(data, sessionRequests.get(data) + 1);
	}

	public String getDetails() {

		String details = "" + //
				"Key: " + getKey() + "\n" + //
				"Created: " + getTimeCreated() + "\n" + //
				"Last Touched: " + getTimeTouched() + "\n";

		for (String key : sessionRequests.keySet()) {

			details += String.format("%s:%s\n", key, sessionRequests.get(key));
		}

		return details;
	}
}
