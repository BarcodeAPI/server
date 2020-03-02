package org.barcodeapi.server.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

		String details = "" + //
				"Key: " + getKey() + "\n\n";
		for (String key : sessionRequests.keySet()) {

			details += String.format("%s:%s\n", key, sessionRequests.get(key));
		}

		return details;
	}
}
