package org.barcodeapi.server.cache;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * LimiterCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class LimiterCache {

	// Default values for new limiters
	private static final int DEFLIMIT_RATE;
	private static final boolean DEFLIMIT_ENFORCE;

	// Local instance of the limiters cache
	private static final ObjectCache _LIMITERS;

	static {

		// Load plan from configuration
		JSONObject freePlan = Config//
				.get(Cfg.Plans).getJSONObject("free");

		// Free plan defaults
		DEFLIMIT_RATE = freePlan.getInt("limit");
		DEFLIMIT_ENFORCE = freePlan.getBoolean("enforce");

		// Get the limiter cache
		_LIMITERS = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);
	}

	public static CachedLimiter getLimiter(Subscriber sub, String address) {
		LibMetrics.hitMethodRunCounter();

		// Determine if limiter exists
		CachedLimiter limiter;
		if (_LIMITERS.has(address)) {

			// Get the existing limiter from the cache
			limiter = (CachedLimiter) _LIMITERS.get(address);
		} else {

			// Determine enforce / limits for the new limiter
			int limit = (sub != null) ? sub.getLimit() : DEFLIMIT_RATE;
			boolean enforce = (sub != null) ? sub.isEnforced() : DEFLIMIT_ENFORCE;

			// Create a new limiter and add it to the cache
			limiter = new CachedLimiter(enforce, address, limit);
			_LIMITERS.put(address, limiter);
		}

		// Return the limiter
		return limiter;
	}
}
