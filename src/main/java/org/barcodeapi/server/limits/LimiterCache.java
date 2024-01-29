package org.barcodeapi.server.limits;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.core.ObjectCache;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

public class LimiterCache {

	private static final String CACHE_IP = "LIMITS-IP";

	public static ObjectCache getIpCache() {
		return ObjectCache.getCache(CACHE_IP);
	}

	public static CachedLimiter getByIp(String caller) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache cache = ObjectCache.getCache(CACHE_IP);
		if (!cache.has(caller)) {
			cache.put(caller, newByIp(caller));
		}

		return (CachedLimiter) cache.get(caller);
	}

	private static CachedLimiter newByIp(String caller) {

		return newLimiter("ips", caller);
	}

	private static final String CACHE_KEY = "LIMITS-KEY";

	public static ObjectCache getKeyCache() {
		return ObjectCache.getCache(CACHE_KEY);
	}

	public static CachedLimiter getByKey(String caller) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache cache = ObjectCache.getCache(CACHE_KEY);
		if (!cache.has(caller)) {
			cache.put(caller, newByKey(caller));
		}

		return (CachedLimiter) cache.get(caller);
	}

	private static CachedLimiter newByKey(String caller) {

		return newLimiter("keys", caller);
	}

	private static CachedLimiter newLimiter(String index, String caller) {

		JSONObject limits = AppConfig.get().getJSONObject("limits");

		long appLimit = limits.getLong("default");

		JSONObject idx = limits.getJSONObject(index);

		long usrLimit = idx.optLong(caller, appLimit);

		return new CachedLimiter(caller, usrLimit);
	}
}
