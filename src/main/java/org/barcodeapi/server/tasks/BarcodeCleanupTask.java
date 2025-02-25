package org.barcodeapi.server.tasks;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.CodeTypes;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * BarcodeCleanupTask.java
 * 
 * A background task which periodically removes stale barcodes from the cache.
 * Additionally saves a cache snapshot to disk, to be used on server restart.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeCleanupTask extends BackgroundTask {

	public BarcodeCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		// Loop each supported type
		int removed = 0, active = 0;
		for (String type : CodeTypes.inst().getTypes()) {

			// Get the type cache
			ObjectCache cache = ObjectCache.getCache(type);

			// Expire and count objects
			removed += cache.expireOldObjects();
			active += cache.count();
		}

		// Log the cache counts
		LibLog._clogF("I2201", removed, active);
	}
}
