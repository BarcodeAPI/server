package org.barcodeapi.server.tasks;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterCleanupTask.java
 * 
 * A background task which periodically removes stale limiters from the cache.
 * Additionally saves a cache snapshot to disk, to be used on server restart.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterCleanupTask extends BackgroundTask {

	public LimiterCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		// Clean IP limiter cache
		cleanLimiterCache(ObjectCache.CACHE_LIMITERS);
	}

	/**
	 * Remove expired objects for a given limiter cache.
	 * 
	 * @param type cache type
	 */
	private void cleanLimiterCache(String type) {

		// Get the requested limiter cache
		ObjectCache cache = ObjectCache.getCache(type);

		// Remove expired objects and log current counts
		int removed = cache.expireOldObjects(), active = cache.count();
		LibLog._clogF("I2601", type, removed, active);
	}
}
