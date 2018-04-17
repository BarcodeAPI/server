package org.barcodeapi.server.cache;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.gen.CodeType;
import org.barcodeapi.server.statistics.StatsCollector;

public class BarcodeCache {

	private static BarcodeCache imageCache;

	private ConcurrentHashMap<CodeType, ConcurrentHashMap<String, CachedObject>> cache;

	/**
	 * Initialize the cache.
	 */
	public BarcodeCache() {

		cache = new ConcurrentHashMap<CodeType, ConcurrentHashMap<String, CachedObject>>();
	}

	/**
	 * Create a cache for a given code type.
	 * 
	 * @param type
	 */
	public void createCache(CodeType type) {

		cache.put(type, new ConcurrentHashMap<String, CachedObject>());
	}

	/**
	 * Lookup an object from a specific type cache. Will return null if not found.
	 * 
	 * @param type
	 * @param data
	 * @return
	 */
	public CachedObject getBarcode(CodeType type, String data) {

		if (!cache.get(type).containsKey(data)) {

			String counterName = "cache." + type.toString() + ".miss";
			StatsCollector.getInstance().incrementCounter(counterName);
			return null;
		}

		String counterName = "cache." + type.toString() + ".hit";
		StatsCollector.getInstance().incrementCounter(counterName);
		return cache.get(type).get(data);
	}

	/**
	 * Add an object to the cache.
	 * 
	 * @param type
	 * @param data
	 * @param image
	 */
	public void addImage(CodeType type, String data, CachedObject image) {

		String counterName = "cache." + type.toString() + ".add";
		StatsCollector.getInstance().incrementCounter(counterName);
		cache.get(type).put(data, image);
	}

	/**
	 * Remove an object from the cache.
	 * 
	 * @param type
	 * @param data
	 */
	public void removeImage(CodeType type, String data) {

		if (getBarcode(type, data) == null) {

			return;
		}

		String counterName = "cache." + type.toString() + ".remove";
		StatsCollector.getInstance().incrementCounter(counterName);
		cache.get(type).remove(data);
	}

	/**
	 * Get the current size of the cache.
	 * 
	 * @return
	 */
	public long getCacheSize() {

		long cacheSize = 0;
		for (CodeType types : cache.keySet()) {

			for (String key : cache.get(types).keySet()) {

				cacheSize += cache.get(types).get(key).getDataSize();
			}
		}

		return cacheSize;
	}

	/**
	 * Get all of the keys in the cache.
	 * 
	 * @return
	 */
	public String[] getCacheKeys() {

		ArrayList<String> keys = new ArrayList<String>();
		for (CodeType type : cache.keySet()) {

			for (String key : cache.get(type).keySet()) {

				keys.add(type.toString() + " : " + key);
			}
		}

		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * Get an instance of the cache.
	 * 
	 * @return
	 */
	public static synchronized BarcodeCache getInstance() {

		if (imageCache == null) {

			imageCache = new BarcodeCache();
		}
		return imageCache;
	}
}
