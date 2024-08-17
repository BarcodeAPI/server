package org.barcodeapi.server.tasks;

import java.io.IOException;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * SessionCleanupTask.java
 * 
 * A background task which periodically removes stale sessions from the cache.
 * Additionally saves a cache snapshot to disk, to be used on server restart.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionCleanupTask extends BackgroundTask {

	private final ObjectCache sessions = //
			ObjectCache.getCache(ObjectCache.CACHE_SESSIONS);

	public SessionCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		// Remove expired objects and log current counts
		int removed = sessions.expireOldObjects(), active = sessions.count();
		LibLog._clogF("I2401", removed, active);

		try {

			// Save cache snapshot
			int saved = sessions.saveSnapshot();
			LibLog._clogF("I2602", sessions.getName(), saved);
		} catch (IOException e) {

			// Log the failure
			LibLog._clog("E2602", e);
		}
	}
}
