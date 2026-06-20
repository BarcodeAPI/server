package org.barcodeapi.server.cache;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * LimiterCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class LimiterCache {

	// Local instance of the limiters cache
	private static final ObjectCache _LIMITERS;

	static {

		// Get the limiter cache
		_LIMITERS = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);
	}

	public static boolean hasCaller(String caller) {
		LibMetrics.hitMethodRunCounter();

		return _LIMITERS.has(caller);
	}

	public static CachedLimiter getLimiter(Subscriber sub, String address) {
		LibMetrics.hitMethodRunCounter();

		// User ID is customer or address
		String caller = (sub != null) ? sub.getCustomer() : address;

		// Determine if limiter exists
		CachedLimiter limiter;
		if (_LIMITERS.has(caller)) {

			// Get the existing limiter from the cache
			limiter = (CachedLimiter) _LIMITERS.get(caller);
		} else {

			// Create a new limiter and add it to the cache
			limiter = new CachedLimiter(sub, address);
			_LIMITERS.put(caller, limiter);
		}

		// Return the limiter
		return limiter;
	}

	public static boolean flushLimiter(String caller) {
		LibMetrics.hitMethodRunCounter();

		if (!_LIMITERS.has(caller)) {
			return false;
		}

		_LIMITERS.remove(caller);
		return true;
	}
}
