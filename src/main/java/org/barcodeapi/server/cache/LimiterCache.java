package org.barcodeapi.server.cache;

import org.barcodeapi.core.AppConfig;
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

	private static final JSONObject CONFIG_IPS = LIMITS_CONFIG.getJSONObject("ips");
	private static final long DEFAULT_LIMIT_IP = CONFIG_IPS.getLong("__default");
	private static final ObjectCache CACHE_IPS = ObjectCache.getCache(ObjectCache.CACHE_IP);

	private static final JSONObject CONFIG_KEYS = LIMITS_CONFIG.getJSONObject("keys");
	private static final long DEFAULT_LIMIT_KEYS = CONFIG_KEYS.getLong("__default");
	private static final ObjectCache CACHE_KEYS = ObjectCache.getCache(ObjectCache.CACHE_KEY);

	public static CachedLimiter getByIp(String caller) {
		LibMetrics.hitMethodRunCounter();

		CachedLimiter limiter;
		if (CACHE_IPS.has(caller)) {
			limiter = (CachedLimiter) CACHE_IPS.get(caller);
		} else {
			CACHE_IPS.put(caller, (limiter = new CachedLimiter(//
					LIMITS_ENFORCE, caller, CONFIG_IPS.optLong(caller, DEFAULT_LIMIT_IP))));
		}

		return limiter;
	}

	public static CachedLimiter getByKey(String caller) {
		LibMetrics.hitMethodRunCounter();

		CachedLimiter limiter;
		if (CACHE_KEYS.has(caller)) {
			limiter = (CachedLimiter) CACHE_KEYS.get(caller);
		} else {
			CACHE_KEYS.put(caller, (limiter = new CachedLimiter(//
					LIMITS_ENFORCE, caller, CONFIG_KEYS.optLong(caller, DEFAULT_LIMIT_KEYS))));
		}

		return limiter;
	}
}
