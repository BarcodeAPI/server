package org.barcodeapi.server.tasks;

import java.io.IOException;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * SessionCleanupTask.java
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

		int removed = sessions.expireOldObjects();
		int active = sessions.count();

		// Log session count
		LibLog._clogF("I2401", removed, active);

		try {

			sessions.saveSnapshot();
		} catch (IOException e) {
		}
	}
}
