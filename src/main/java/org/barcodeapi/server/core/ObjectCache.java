package org.barcodeapi.server.core;

import org.barcodeapi.server.core.Log.LOG;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCache {

	private static ConcurrentHashMap<String, ObjectCache> caches = new ConcurrentHashMap<>();

	private final String name;
	private final ConcurrentHashMap<String, CachedObject> cache;

	public ObjectCache(String name) {
		this.name = name;
		this.cache = new ConcurrentHashMap<>();
	}

	public String getName() {
		return name;
	}

	public void put(String key, CachedObject value) {
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
		return cache.get(key);
	}

	public CachedObject remove(String key) {
		return cache.remove(key);
	}

	public static synchronized ObjectCache getCache(String name) {
		if (!caches.containsKey(name)) {
			Log.out(LOG.SERVER, "Initialized cache: " + name);
			caches.put(name, new ObjectCache(name));
		}

		return caches.get(name);
	}
}
