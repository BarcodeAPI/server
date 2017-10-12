package org.barcodeapi.server.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionObject {

	private final String key;

	private ConcurrentHashMap<String, Integer> renderRequests;

	public SessionObject() {

		key = UUID.randomUUID().toString();

		renderRequests = new ConcurrentHashMap<String, Integer>();
	}

	public String getKey() {

		return key;
	}

	public void onRender(String data) {

		if (!renderRequests.containsKey(data)) {

			renderRequests.put(data, 1);
			return;
		}

		renderRequests.put(data, renderRequests.get(data) + 1);
	}

	public String getDetails() {

		String details = "";
		for (String key : renderRequests.keySet()) {

			details += key + " : " + renderRequests.get(key) + "\n";
		}

		return details;
	}
}
