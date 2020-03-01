package org.barcodeapi.server.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.barcodeapi.server.statistics.StatsCollector;

public class ObjectCache {

	private static ConcurrentHashMap<String, ObjectCache> caches = new ConcurrentHashMap<>();

	private final String name;
	private final StatsCollector stats;
	private final ConcurrentHashMap<String, CachedObject> cache;

	public ObjectCache(String name) {

		this.name = name;
		this.stats = StatsCollector.getInstance();
		this.cache = new ConcurrentHashMap<>();
	}

	public String getName() {

		return name;
	}

	public void put(String key, CachedObject value) {

		stats.incrementCounter("cache." + name + ".add");
		cache.put(key, value);
	}

	public CachedObject get(String key) {

		if (cache.containsKey(key)) {
			stats.incrementCounter("cache." + name + ".hit");
		} else {
			stats.incrementCounter("cache." + name + ".miss");
		}

		return cache.get(key);
	}

	public KeySetView<String, CachedObject> getKeys() {
		return cache.keySet();
	}

	public CachedObject remove(String key) {

		stats.incrementCounter("cache." + name + ".remove");
		return cache.remove(key);
	}

	public static synchronized ObjectCache getCache(String name) {

		if (!caches.containsKey(name)) {
			caches.put(name, new ObjectCache(name));
		}

		return caches.get(name);
	}
}
