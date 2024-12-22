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
 * A user session object allowing for the detailed tracking of individual user
 * usage activities across all handlers/
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedSession extends CachedObject {

	private static final long serialVersionUID = 20241123L;

	private static final int OBJECT_LIFE_STD = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("session").getInt("life");

	private static final int OBJECT_LIFE_SHORT = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("session").getInt("shortLife");

	private final String key;

	private final Cookie cookie;

	private final ConcurrentHashMap<String, Integer> sessionRequests;

	public CachedSession() {

		this.key = UUID.randomUUID().toString();
		this.cookie = new Cookie("session", this.key);
		this.sessionRequests = new ConcurrentHashMap<String, Integer>();

		this.setStandardTimeout(OBJECT_LIFE_STD, TimeUnit.MINUTES);
		this.setShortLivedTimeout(OBJECT_LIFE_SHORT, TimeUnit.MINUTES);
	}

	/**
	 * Returns the key for the session.
	 * 
	 * @return the key for the session
	 */
	public String getKey() {

		return key;
	}

	/**
	 * Returns the browser cookie for the session.
	 * 
	 * @return the browser cookie for the session
	 */
	public Cookie getCookie() {

		return cookie;
	}

	/**
	 * Called when the user session is loaded when navigating across handlers.
	 * Tracks a users usage across the site, viewable through the /session/ handler.
	 * 
	 * @param data
	 */
	public void hit(String data) {

		this.touch();
		if (!sessionRequests.containsKey(data)) {

			sessionRequests.put(data, 1);
			return;
		}

		sessionRequests.put(data, sessionRequests.get(data) + 1);
	}

	/**
	 * Returns the user session as a JSON object.
	 * 
	 * @return the user session in JSON format
	 */
	public JSONObject asJSON() {

		int requestCount = 0;
		JSONArray requests = new JSONArray();
		for (Map.Entry<String, Integer> entry : sessionRequests.entrySet()) {

			requestCount += entry.getValue();
			requests.put(new JSONObject()//
					.put("text", entry.getKey())//
					.put("hits", entry.getValue()));
		}

		return (new JSONObject()//
				.put("key", getKey())//
				.put("created", getTimeCreated())//
				.put("expires", getTimeExpires())//
				.put("last", getTimeLastTouched())//
				.put("count", requestCount)//
				.put("requests", requests));
	}
}
