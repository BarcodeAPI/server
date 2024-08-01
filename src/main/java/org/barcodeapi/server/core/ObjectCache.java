package org.barcodeapi.server.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * ObjectCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ObjectCache {

	private static final LibMetrics stats = LibMetrics.instance();

	private static ConcurrentHashMap<String, ObjectCache> caches = new ConcurrentHashMap<>();

	private final String name;
	private final File cacheFile;

	private final ConcurrentHashMap<String, CachedObject> cache;

	@SuppressWarnings("unchecked")
	public ObjectCache(String name) {

		this.name = name;
		this.cacheFile = (new File(//
				String.format("cache-%s.snap", name)));

		ConcurrentHashMap<String, CachedObject> c;

		try {
			FileInputStream st = new FileInputStream(cacheFile);
			ObjectInputStream str = new ObjectInputStream(st);

			// Read the cache file
			c = ((ConcurrentHashMap<String, CachedObject>) str.readObject());

			str.close();
			st.close();

		} catch (Exception e) {

			// Create a new cache
			c = new ConcurrentHashMap<String, CachedObject>();
		}

		this.cache = c;
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
		LibMetrics.hitMethodRunCounter();

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

	public boolean has(String key) {
		return cache.containsKey(key);
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

	public int snapshot() throws IOException {

		FileOutputStream st = new FileOutputStream(cacheFile);
		ObjectOutputStream str = new ObjectOutputStream(st);

		int count;
		synchronized (cache) {

			count = cache.size();
			str.writeObject(cache);
		}

		str.close();
		st.close();

		return count;
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

			LibLog._clogF("I0101", name);
			caches.put(name, new ObjectCache(name));
		}

		return caches.get(name);
	}
}
