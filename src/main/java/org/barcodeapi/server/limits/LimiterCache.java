package org.barcodeapi.server.limits;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.core.ObjectCache;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * LimiterCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterCache {

	private static final JSONObject LIMITS_CONFIG = AppConfig.get().getJSONObject("limits");

	private static final boolean LIMITS_ENFORCE = LIMITS_CONFIG.getBoolean("enforce");

	private static final String CACHE_IP = "LIMITS-IP";
	private static final String CACHE_KEY = "LIMITS-KEY";

	public static ObjectCache getIpCache() {
		return ObjectCache.getCache(CACHE_IP);
	}

	public static ObjectCache getKeyCache() {
		return ObjectCache.getCache(CACHE_KEY);
	}

	public static CachedLimiter getByIp(String caller) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache cache = ObjectCache.getCache(CACHE_IP);
		if (!cache.has(caller)) {
			cache.put(caller, newLimiter("ips", caller));
		}

		return (CachedLimiter) cache.get(caller);
	}

	public static CachedLimiter getByKey(String caller) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache cache = ObjectCache.getCache(CACHE_KEY);
		if (!cache.has(caller)) {
			cache.put(caller, newLimiter("keys", caller));
		}

		return (CachedLimiter) cache.get(caller);
	}

	private static CachedLimiter newLimiter(String index, String caller) {

		JSONObject idx = LIMITS_CONFIG.getJSONObject(index);

		long defaultLimit = idx.getLong("__default");

		long userLimit = idx.optLong(caller, defaultLimit);

		return new CachedLimiter(caller, userLimit, LIMITS_ENFORCE);
	}
}
