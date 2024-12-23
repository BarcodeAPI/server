package org.barcodeapi.server.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CachedSession.java
 * 
 * A user session object allowing for the detailed tracking of individual user
 * usage activities across all handlers.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedSession extends CachedObject {

	private static final long serialVersionUID = 20241222L;

	private final String key;

	private final Cookie cookie;

	private final ConcurrentHashMap<String, Integer> sessionRequests;

	public CachedSession() {
		super("session");

		// Generate new random UUID
		this.key = UUID.randomUUID().toString();

		// Create user cookie
		this.cookie = new Cookie("session", this.key);
		this.cookie.setPath("/");

		// Memory map for request history
		this.sessionRequests = new ConcurrentHashMap<String, Integer>();
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

		// Check if first entry for key
		if (!sessionRequests.containsKey(data)) {

			// Set counter to 1
			sessionRequests.put(data, 1);
			return;
		}

		// Get and increment counter
		sessionRequests.put(data, //
				(sessionRequests.get(data) + 1));
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
