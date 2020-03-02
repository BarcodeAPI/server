package org.barcodeapi.server.tasks;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.ObjectCache;

public class CacheCleanupTask extends BackgroundTask {

	@Override
	public void onRun() {

		int removed = 0;
		ObjectCache sessions = ObjectCache.getCache("sessions");
		for (String key : sessions.getKeys()) {

			if (sessions.get(key).isExpired()) {
				CachedObject o = sessions.remove(key);
				if (o != null) {
					removed++;
				}
			}
		}

		Log.out(LOG.SERVER, "Cleared [ " + removed + " ] sessions");
	}
}
