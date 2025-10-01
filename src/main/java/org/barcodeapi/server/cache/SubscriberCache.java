package org.barcodeapi.server.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;

public class SubscriberCache {

	private static final Map<String, Subscriber> subscribersByName;
	private static final Map<String, Subscriber> subscribersByIP;
	private static final Map<String, Subscriber> subscribersByKey;

	static {
		subscribersByName = new ConcurrentHashMap<>();
		subscribersByIP = new ConcurrentHashMap<>();
		subscribersByKey = new ConcurrentHashMap<>();

		reload();
	}

	/**
	 * Reload subscriber info from configuration on disk.
	 */
	public static void reload() {

		// Flush existing subscribers
		subscribersByName.clear();
		subscribersByIP.clear();
		subscribersByKey.clear();

		// Load subscribers, force reload
		JSONArray users = Config//
				.get(Cfg.Subscribers, true)//
				.getJSONArray("subscribers");

		// Loop and cache all subscribers
		Subscriber subscriber;
		for (int x = 0; x < users.length(); x++) {

			// Get the subscriber definition from the array
			JSONObject rawSub = users.getJSONObject(x);

			try {

				// Load the subscriber info
				subscriber = new Subscriber(rawSub);
			} catch (Exception | Error e) {

				// Log if failed to load subscriber
				LibLog._log("Failed to load subscriber.", e);
				LibLog._logF("Subscriber: %s", rawSub);
				continue;
			}

			// Skip if inactive
			if (!subscriber.getActive()) {
				continue;
			}

			// Map of Customer Names
			subscribersByName.put(subscriber.getCustomer(), subscriber);

			// Map Customer IPs
			JSONArray ips = subscriber.getIPs();
			for (int y = 0; y < ips.length(); y++) {
				subscribersByIP.put(ips.getString(y), subscriber);
			}

			// Map Customer Keys
			JSONArray keys = subscriber.getKeys();
			for (int y = 0; y < keys.length(); y++) {
				subscribersByKey.put(keys.getString(y), subscriber);
			}
		}
	}

	/**
	 * Lookup a subscriber by customer name.
	 * 
	 * @param name name of the customer
	 * @return the subscriber info
	 */
	public static Subscriber getByName(String name) {
		return subscribersByName.get(name);
	}

	/**
	 * Lookup a subscriber by API key.
	 * 
	 * @param key the API key
	 * @return the subscriber info
	 */
	public static Subscriber getByKey(String key) {
		return subscribersByKey.get(key);
	}

	/**
	 * Lookup a subscriber by IP address.
	 * 
	 * @param ip the IP address
	 * @return the subscriber info
	 */
	public static Subscriber getByIP(String ip) {
		return subscribersByIP.get(ip);
	}
}
