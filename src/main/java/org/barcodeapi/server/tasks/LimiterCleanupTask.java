package org.barcodeapi.server.tasks;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterCleanupTask.java
 * 
 * A background task which periodically removes stale limiters from the cache.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class LimiterCleanupTask extends BackgroundTask {

	public LimiterCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		// Get the requested limiter cache
		ObjectCache cache = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);

		// Remove expired objects and log current counts
		int removed = cache.expireOldObjects();
		int active = cache.count();

		// Log the session cache info
		LibLog._clogF("I2601", removed, active);
	}
}
