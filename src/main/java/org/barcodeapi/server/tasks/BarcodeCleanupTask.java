package org.barcodeapi.server.tasks;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.gen.CodeType;

import com.mclarkdev.tools.liblog.LibLog;

public class BarcodeCleanupTask extends BackgroundTask {

	public BarcodeCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		int active = 0;
		int removed = 0;
		for (CodeType type : CodeType.values()) {
			ObjectCache cache = BarcodeCache.getCache(type);
			removed += cache.expireOldObjects();
			active += cache.count();
		}

		System.gc();
		LibLog._clogF("I2201", removed, active);
	}
}
