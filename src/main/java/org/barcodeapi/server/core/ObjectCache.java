package org.barcodeapi.server.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.statistics.StatsCollector;

import com.mclarkdev.tools.liblog.LibLog;

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

		stats.hitCounter("cache", name, "add");
		cache.put(key, value);
	}

	public int count() {

		return cache.size();
	}

	public int expireOldObjects() {

		int removed = 0;
		for (Map.Entry<String, CachedObject> entry : cache.entrySet()) {

			// skip if not expired
			if (!entry.getValue().isExpired()) {
				continue;
			}

			// skip if not removed
			if (remove(entry.getKey()) == null) {
				continue;
			}

			removed++;
		}

		return removed;
	}

	public CachedObject get(String key) {

		if (cache.containsKey(key)) {
			stats.hitCounter("cache", name, "hit");
		} else {
			stats.hitCounter("cache", name, "miss");
		}

		return cache.get(key);
	}

	public ConcurrentHashMap<String, CachedObject> getRawCache() {
		return cache;
	}

	public CachedObject remove(String key) {

		stats.hitCounter("cache", name, "remove");
		return cache.remove(key);
	}

	public double clearCache() {

		double cleared = 0;
		synchronized (cache) {

			cleared = cache.size();
			stats.hitCounter(cleared, "cache", name, "clear");
			cache.clear();
		}
		return cleared;
	}

	public static synchronized ObjectCache getCache(String name) {

		if (!caches.containsKey(name)) {

			LibLog.clogF_("I0101", name);
			caches.put(name, new ObjectCache(name));
		}

		return caches.get(name);
	}
}
