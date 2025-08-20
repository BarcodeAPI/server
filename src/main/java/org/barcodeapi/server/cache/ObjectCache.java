package org.barcodeapi.server.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.core.AppConfig;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * ObjectCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ObjectCache {

	public static final String CACHE_SHARE = "_shares";
	public static final String CACHE_SESSIONS = "_sessions";
	public static final String CACHE_LIMITERS = "_limiters";

	private static final String SNAPSHOT_DIR = AppConfig.get()//
			.getJSONObject("cache").getString("_snapshots");

	private static final LibMetrics stats = LibMetrics.instance();

	private static final ConcurrentHashMap<String, ObjectCache> caches;

	private static final File cacheDir;

	static {
		caches = new ConcurrentHashMap<>();
		cacheDir = new File(SNAPSHOT_DIR);
	}

	private final String name;
	private final File cacheFile;

	private final ConcurrentHashMap<String, CachedObject> cache;

	public ObjectCache(String name) {

		this.name = name;
		this.cacheFile = new File(cacheDir, //
				String.format("cache-%s.snap", name));

		ConcurrentHashMap<String, CachedObject> c = loadSnapshot();
		c = (c != null) ? c : new ConcurrentHashMap<String, CachedObject>();

		this.cache = c;
	}

	public String getName() {

		return name;
	}

	public ConcurrentHashMap<String, CachedObject> raw() {
		return cache;
	}

	public void put(String key, CachedObject value) {

		stats.hitCounter("cache", name, "add");
		cache.put(key, value);
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

	public CachedObject remove(String key) {

		stats.hitCounter("cache", name, "remove");
		return cache.remove(key);
	}

	public int saveSnapshot() throws IOException {

		// Make directory
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		// Delete old cache file
		if (cacheFile.exists()) {
			cacheFile.delete();
		}

		// Open file output streams
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

	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<String, CachedObject> loadSnapshot() {

		if (!cacheFile.exists()) {
			return null;
		}

		// Open cache file streams
		ConcurrentHashMap<String, CachedObject> c = null;
		try (FileInputStream st = new FileInputStream(cacheFile)) {
			ObjectInputStream str = new ObjectInputStream(st);

			// Read the cache file as a map
			c = ((ConcurrentHashMap<String, CachedObject>) str.readObject());

		} catch (Exception e) {

			// Log the failure
			LibLog._clog("E2603", e);
		}

		return c;
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

	public static String[] getCacheNames() {

		return caches.keySet().toArray(//
				new String[caches.size()]);
	}

	public static synchronized ObjectCache getCache(String name) {

		if (!caches.containsKey(name)) {

			LibLog._clogF("I0101", name);
			caches.put(name, new ObjectCache(name));
		}

		return caches.get(name);
	}
}
