package org.barcodeapi.server.limits;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.ObjectCache;
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

	private static final long DEFAULT_LIMIT_IP = LIMITS_CONFIG.getJSONObject("ips").getLong("__default");

	private static final long DEFAULT_LIMIT_KEYS = LIMITS_CONFIG.getJSONObject("keys").getLong("__default");

	private static final ObjectCache CACHE_IPS = ObjectCache.getCache("ips");

	private static final ObjectCache CACHE_KEYS = ObjectCache.getCache("keys");

	public static CachedLimiter getByIp(String caller) {
		LibMetrics.hitMethodRunCounter();

		CachedLimiter limiter;
		if (!CACHE_IPS.has(caller)) {
			limiter = (CachedLimiter) CACHE_IPS.get(caller);
		} else {
			CACHE_IPS.put(caller, (limiter = //
					newLimiter(CACHE_IPS.getName(), caller, DEFAULT_LIMIT_IP)));
		}

		return limiter;
	}

	public static CachedLimiter getByKey(String caller) {
		LibMetrics.hitMethodRunCounter();

		CachedLimiter limiter;
		if (!CACHE_KEYS.has(caller)) {
			limiter = (CachedLimiter) CACHE_KEYS.get(caller);
		} else {
			CACHE_KEYS.put(caller, (limiter = //
					newLimiter(CACHE_KEYS.getName(), caller, DEFAULT_LIMIT_KEYS)));
		}

		return limiter;
	}

	private static CachedLimiter newLimiter(String index, String caller, long defaultLimit) {
		LibMetrics.hitMethodRunCounter();

		long userLimit = LIMITS_CONFIG//
				.getJSONObject(index).optLong(caller, defaultLimit);
		return new CachedLimiter(LIMITS_ENFORCE, caller, userLimit);
	}
}
