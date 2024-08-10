package org.barcodeapi.server.tasks;

import java.io.IOException;

import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.CodeTypes;

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

		// Loop each supported type
		int active = 0, removed = 0;
		for (String type : CodeTypes.inst().getTypes()) {

			// Get the type cache
			ObjectCache cache = ObjectCache.getCache(type);

			// Expire and count objects
			removed += cache.expireOldObjects();
			active += cache.count();

			try {

				// Save cache snapshot
				int saved = cache.saveSnapshot();
				LibLog._logF("Cache snapshot saved. (%s : %d)", type, saved);
			} catch (IOException e) {

				// Log failure.
				LibLog._log("Failed to save barcode cache.", e);
			}
		}

		// Log the counts
		LibLog._clogF("I2201", removed, active);
	}
}
