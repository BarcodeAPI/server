package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.session.SessionCache;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * SessionCleanupTask.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class SessionCleanupTask extends BackgroundTask {

	public SessionCleanupTask() {
		super();
	}

	@Override
	public void onRun() {

		ObjectCache sessions = SessionCache.getCache();
		int removed = sessions.expireOldObjects();
		int active = sessions.count();

		LibLog._clogF("I2401", removed, active);
	}
}
