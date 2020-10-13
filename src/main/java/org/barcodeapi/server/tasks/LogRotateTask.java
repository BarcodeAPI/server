package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.Log;

public class LogRotateTask extends BackgroundTask {

	public LogRotateTask() {
		super();
	}

	@Override
	public void onRun() {
		Log.rollLogs();
	}
}
