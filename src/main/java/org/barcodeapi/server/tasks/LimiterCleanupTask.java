package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.limits.LimiterCache;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterCleanupTask.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterCleanupTask extends BackgroundTask {

	public LimiterCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		// Cleanup IP caches
		ObjectCache byIp = LimiterCache.getIpCache();
		int removedByIp = byIp.expireOldObjects();
		int activeByIp = byIp.count();
		LibLog._clogF("I2601", "IP", removedByIp, activeByIp);

		// Cleanup Key caches
		ObjectCache byKey = LimiterCache.getKeyCache();
		int removedByKey = byKey.expireOldObjects();
		int activeByKey = byKey.count();
		LibLog._clogF("I2601", "KEY", removedByKey, activeByKey);
	}
}
