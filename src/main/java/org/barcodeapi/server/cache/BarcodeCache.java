package org.barcodeapi.server.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.gen.CodeType;
import org.barcodeapi.server.statistics.StatsCollector;

public class BarcodeCache {

	private final String CACHE_DIR = "/tmp/codeCache";

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

		File cacheDir = new File(CACHE_DIR + "/" + type.toString());
		if (!cacheDir.exists()) {

			cacheDir.mkdir();
		}
	}

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

	public void addImage(CodeType type, String data, CachedObject image) {

		String counterName = "cache." + type.toString() + ".add";
		StatsCollector.getInstance().incrementCounter(counterName);
		cache.get(type).put(data, image);
	}

	public void removeImage(CodeType type, String data) {

		if (getBarcode(type, data) == null) {

			return;
		}

		String counterName = "cache." + type.toString() + ".remove";
		StatsCollector.getInstance().incrementCounter(counterName);
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

	public String[] getCacheKeys() {

		ArrayList<String> keys = new ArrayList<String>();
		for (CodeType type : cache.keySet()) {

			for (String key : cache.get(type).keySet()) {

				keys.add(type.toString() + " : " + key);
			}
		}

		return keys.toArray(new String[keys.size()]);
	}

	public static synchronized BarcodeCache getInstance() {

		if (imageCache == null) {

			imageCache = new BarcodeCache();
		}
		return imageCache;
	}
}
