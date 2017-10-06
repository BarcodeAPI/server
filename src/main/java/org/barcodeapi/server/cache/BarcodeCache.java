package org.barcodeapi.server.cache;

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

	public CachedObject getBarcode(CodeType type, String data) {

		if (cache.get(type).containsKey(data)) {

			String counterName = "cache." + type.toString() + ".hit";
			StatsCollector.getInstance().incrementCounter(counterName);
		}

		return cache.get(type).get(data);
	}

	public void addImage(CodeType type, String data, CachedObject image) {

		StatsCollector.getInstance().incrementCounter("cache.add");

		cache.get(type).put(data, image);
	}

	public void removeImage(CodeType type, String data) {

		StatsCollector.getInstance().incrementCounter("cache.remove");

		cache.get(type).remove(data);
	}

	public long getCacheSize() {

		long cacheSize = 0;
		for (CodeType types : cache.keySet()) {

			for (String key : cache.get(types).keySet()) {

				cacheSize += cache.get(types).get(key).getDataSize();
			}
		}

		return cacheSize;
	}

	public static synchronized BarcodeCache getInstance() {

		if (imageCache == null) {

			imageCache = new BarcodeCache();
		}
		return imageCache;
	}
}
