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
public class LimiterCleanupTask extends BackgroundTask {

	public LimiterCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		try {
			// Cleanup IP caches
			ObjectCache byIp = ObjectCache.getCache(ObjectCache.CACHE_IP);
			int removedByIp = byIp.expireOldObjects(), activeByIp = byIp.count();
			LibLog._clogF("I2601", "IP", removedByIp, activeByIp);
			byIp.saveSnapshot();
		} catch (IOException e) {
		}

		try {
			// Cleanup Key caches
			ObjectCache byKey = ObjectCache.getCache(ObjectCache.CACHE_KEY);
			int removedByKey = byKey.expireOldObjects(), activeByKey = byKey.count();
			LibLog._clogF("I2601", "KEY", removedByKey, activeByKey);
			byKey.saveSnapshot();
		} catch (IOException e) {
		}
	}
}
