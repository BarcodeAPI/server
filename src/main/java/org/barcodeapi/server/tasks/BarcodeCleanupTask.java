package org.barcodeapi.server.tasks;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.ObjectCache;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * BarcodeCleanupTask.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeCleanupTask extends BackgroundTask {

	public BarcodeCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		int active = 0;
		int removed = 0;
		for (String type : CodeTypes.inst().getTypes()) {
			ObjectCache cache = BarcodeCache.getCache(type);
			removed += cache.expireOldObjects();
			active += cache.count();
		}

		LibLog._clogF("I2201", removed, active);
	}
}
