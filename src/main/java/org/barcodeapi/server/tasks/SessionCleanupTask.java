package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.session.SessionCache;

import com.mclarkdev.tools.liblog.LibLog;

public class SessionCleanupTask extends BackgroundTask {

	public SessionCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		ObjectCache sessions = SessionCache.getCache();
		int removed = sessions.expireOldObjects();
		int active = sessions.count();

		System.gc();
		LibLog.clogF_("I2401", removed, active);
	}
}
