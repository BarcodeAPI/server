package org.barcodeapi.server.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.barcodeapi.core.AppConfig;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CachedSession.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedSession extends CachedObject {

	private static final long serialVersionUID = 1L;

	private static final int OBJECT_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("session").getInt("life");

	private final String key;

	private final Cookie cookie;

	private final ConcurrentHashMap<String, Integer> sessionRequests;

	public CachedSession() {
		this.setTimeout(OBJECT_LIFE, TimeUnit.MINUTES);

		this.key = UUID.randomUUID().toString();
		this.cookie = new Cookie("session", this.key);
		this.sessionRequests = new ConcurrentHashMap<String, Integer>();
	}

	public String getKey() {

		return key;
	}

	public Cookie getCookie() {

		return cookie;
	}

	public void hit(String data) {

		this.touch();
		if (!sessionRequests.containsKey(data)) {

			sessionRequests.put(data, 1);
			return;
		}

		sessionRequests.put(data, sessionRequests.get(data) + 1);
	}

	public JSONObject asJSON() {

		int requestCount = 0;
		JSONArray requests = new JSONArray();
		for (Map.Entry<String, Integer> entry : sessionRequests.entrySet()) {

			requestCount += entry.getValue();
			requests.put(new JSONObject()//
					.put("text", entry.getKey())//
					.put("hits", entry.getValue()));
		}

		return new JSONObject()//
				.put("sessionKey", getKey())//
				.put("timeCreated", getTimeCreated())//
				.put("timeLastSeen", getTimeLastTouched())//
				.put("timeExpires", getTimeExpires())//
				.put("requestCount", requestCount)//
				.put("requestList", requests);
	}
}
