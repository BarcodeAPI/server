package org.barcodeapi.server.tasks;

import java.io.IOException;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterCleanupTask.java
 * 
 * A background task which periodically removed stale limiters from the cache.
 * Additionally saves a cache snapshot to disk, to be used on crash recovery.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterCleanupTask extends BackgroundTask {

	public LimiterCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		cleanLimiterCache(ObjectCache.CACHE_IP);
		cleanLimiterCache(ObjectCache.CACHE_KEY);
	}

	private void cleanLimiterCache(String type) {

		// Get the requested limiter cache
		ObjectCache cache = ObjectCache.getCache(type);

		// Remove expired objects and log current count
		int removed = cache.expireOldObjects(), active = cache.count();
		LibLog._clogF("I2601", type, removed, active);

		try {

			// Save cache snapshot
			int saved = cache.saveSnapshot();
			LibLog._clogF("I2602", type, saved);
		} catch (IOException e) {

			// Log the failure
			LibLog._clog("E2602", e);
		}
	}
}
