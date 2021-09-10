package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.session.SessionCache;

import java.util.TimerTask;

public class SessionCleanupTask extends TimerTask {

	public SessionCleanupTask() {
		super();
	}

	@Override
	public void run() {

		ObjectCache sessions = SessionCache.getCache();
		int removed = sessions.expireOldObjects();
		int active = sessions.count();

		System.gc();
		Log.out(LOG.SERVER, "SESSIONS : Stale [ " + removed + " ] Active [ " + active + " ]");
	}
}
