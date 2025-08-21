package org.barcodeapi.server.cache;

import org.barcodeapi.core.AppConfig;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * LimiterCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class LimiterCache {

	private static final JSONObject LIMITS_CONFIG = AppConfig.get().getJSONObject("limits");

	private static final int DEFLIMIT_RATE = LIMITS_CONFIG.getInt("default");
	private static final boolean DEFLIMIT_ENFORCE = LIMITS_CONFIG.getBoolean("enforce");

	// Local instance of the limiters cache
	private static final ObjectCache LIMITERS = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);

	public static CachedLimiter getLimiter(Subscriber sub, String userID) {
		LibMetrics.hitMethodRunCounter();

		CachedLimiter limiter;

		// Determine if limiter exists
		if (LIMITERS.has(userID)) {

			// Get the existing limiter from the cache
			limiter = (CachedLimiter) LIMITERS.get(userID);
		} else {

			// Determine enforce / limits for the new limiter
			int limit = (sub != null) ? sub.getLimit() : DEFLIMIT_RATE;
			boolean enforce = (sub != null) ? sub.getEnforce() : DEFLIMIT_ENFORCE;

			// Create a new limiter and add it to the cache
			limiter = new CachedLimiter(enforce, userID, limit);
			LIMITERS.put(userID, limiter);
		}

		// Return the limiter
		return limiter;
	}
}
