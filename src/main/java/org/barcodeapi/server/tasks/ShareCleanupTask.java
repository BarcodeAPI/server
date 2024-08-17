package org.barcodeapi.server.tasks;

import java.io.IOException;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * ShareCleanupTask.java
 * 
 * A background task which periodically removes stale shares from the cache.
 * Additionally saves a cache snapshot to disk, to be used on server restart.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ShareCleanupTask extends BackgroundTask {

	private final ObjectCache shares = //
			ObjectCache.getCache(ObjectCache.CACHE_SHARE);

	public ShareCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		// Cleanup Share cache
		int removed = shares.expireOldObjects(), active = shares.count();
		LibLog._clogF("I2611", removed, active);

		try {

			// Save cache snapshot
			int saved = shares.saveSnapshot();
			LibLog._clogF("I2602", shares.getName(), saved);
		} catch (IOException e) {

			// Log the failure
			LibLog._clog("E2602", e);
		}
	}
}
