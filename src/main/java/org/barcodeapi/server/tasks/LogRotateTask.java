package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.Log;

import java.util.TimerTask;

public class LogRotateTask extends TimerTask {

	public LogRotateTask() {
		super();
	}

	@Override
	public void run() {
		Log.rollLogs();
	}
}
