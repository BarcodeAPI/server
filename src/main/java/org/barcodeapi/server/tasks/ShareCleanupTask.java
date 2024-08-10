package org.barcodeapi.server.tasks;

import java.io.IOException;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterCleanupTask.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ShareCleanupTask extends BackgroundTask {

	public ShareCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		try {
			// Cleanup IP caches
			ObjectCache shares = ObjectCache.getCache(ObjectCache.CACHE_SHARE);
			int removed = shares.expireOldObjects(), active = shares.count();
			LibLog._clogF("I2611", removed, active);
			shares.saveSnapshot();
		} catch (IOException e) {
		}
	}
}
