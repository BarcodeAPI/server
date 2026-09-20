package org.barcodeapi.server.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.barcodeapi.server.core.CidrTree;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * SubscriberCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class SubscriberCache {

	private static final Map<String, Subscriber> subscribersByKey;
	private static final Map<String, Subscriber> subscribersByApp;
	private static final CidrTree<Subscriber> subscribersByIP;

	static {

		subscribersByIP = new CidrTree<>();
		subscribersByApp = new ConcurrentHashMap<>();
		subscribersByKey = new ConcurrentHashMap<>();

		reload();
	}

	/**
	 * Reload subscriber info from configuration on disk.
	 */
	public static void reload() {
		long timeStart = System.currentTimeMillis();

		// Flush existing subscribers
		subscribersByIP.clear();
		subscribersByApp.clear();
		subscribersByKey.clear();

		// Load subscribers, force reload
		JSONArray users = Config//
				.get(Cfg.Subscribers, true)//
				.getJSONArray("subscribers");

		// Loop and cache all subscribers
		Subscriber subscriber;
		for (int x = 0; x < users.length(); x++) {

			// Get the subscriber definition from the array
			JSONObject subInfo = users.getJSONObject(x);

			try {

				// Load the subscriber info
				subscriber = new Subscriber(subInfo);
			} catch (Exception | Error e) {

				// Log if failed to load subscriber
				LibLog._log("Failed to load subscriber.", e);
				LibLog._logF("Subscriber: %s", subInfo.toString());
				continue;
			}

			// Skip if inactive
			if (!subscriber.isActive()) {
				continue;
			}

			// Map Customer IPs
			JSONArray subIps = subscriber.getIPs();
			for (int y = 0; y < subIps.length(); y++) {
				subscribersByIP.put(//
						subIps.getString(y), subscriber);
			}

			// Map Customer Applications
			JSONArray subApps = subscriber.getApps();
			for (int y = 0; y < subApps.length(); y++) {
				subscribersByApp.put(//
						subApps.getString(y), subscriber);
			}

			// Map Customer Keys
			JSONArray subKeys = subscriber.getKeys();
			for (int y = 0; y < subKeys.length(); y++) {
				subscribersByKey.put(//
						subKeys.getString(y), subscriber);
			}
		}
		
		long timeDiff = (System.currentTimeMillis() - timeStart);
		LibLog._logF("Subscribers reloaded in %dms", timeDiff);
	}

	/**
	 * Lookup a subscriber by API key.
	 * 
	 * @param key the API key
	 * @return the subscriber info or null
	 */
	public static Subscriber getByKey(String key) {
		return subscribersByKey.get(key);
	}

	/**
	 * Lookup a subscriber by IP address.
	 * 
	 * @param ip the IP address
	 * @return the subscriber info or null
	 */
	public static Subscriber getByIP(String ip) {
		return subscribersByIP.get(ip);
	}

	/**
	 * Lookup a subscriber by the referring application.
	 * 
	 * @param app the application address
	 * @return the subscriber info or null
	 */
	public static Subscriber getByApp(String app) {
		return subscribersByApp.get(app);
	}
}
