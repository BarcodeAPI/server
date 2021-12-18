package org.barcodeapi.server.tasks;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.gen.CodeType;

import java.util.TimerTask;

public class BarcodeCleanupTask extends TimerTask {

	public BarcodeCleanupTask() {
		super();
	}

	@Override
	public void run() {

		int active = 0;
		int removed = 0;
		for (CodeType type : CodeType.values()) {
			ObjectCache cache = BarcodeCache.getCache(type);
			removed += cache.expireOldObjects();
			active += cache.count();
		}

		System.gc();
		Log.out(LOG.SERVER, "CACHE : Stale [ " + removed + " ] Active [ " + active + " ]");
	}
}
