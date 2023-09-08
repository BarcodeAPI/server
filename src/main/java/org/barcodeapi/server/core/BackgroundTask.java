package org.barcodeapi.server.core;

import java.util.TimerTask;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

public abstract class BackgroundTask extends TimerTask {

	private final LibMetrics stats = LibMetrics.instance();

	public BackgroundTask() {

	}

	public LibMetrics getStats() {
		return stats;
	}

	@Override
	public void run() {

		// log task name
		String taskName = getClass().getName();
		taskName = taskName.substring(taskName.lastIndexOf('.') + 1);
		LibLog._clogF("I2001", taskName);
		long timeStart = System.currentTimeMillis();

		// call implemented method
		onRun();

		// hit the counter
		getStats().hitCounter("task", taskName, "count");
		long timeTask = System.currentTimeMillis() - timeStart;
		getStats().hitCounter(timeTask, "task", taskName, "time");
	}

	public abstract void onRun();
}
