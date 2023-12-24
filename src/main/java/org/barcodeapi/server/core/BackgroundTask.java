package org.barcodeapi.server.core;

import java.util.TimerTask;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

public abstract class BackgroundTask extends TimerTask {

	private final LibMetrics stats = LibMetrics.instance();

	private final String name;

	public BackgroundTask() {

		String clazz = getClass().getName();
		name = clazz.substring(clazz.lastIndexOf('.') + 1);
	}

	public LibMetrics getStats() {
		return stats;
	}

	public String getName() {
		return name;
	}

	@Override
	public void run() {

		// log task name`
		LibLog._clogF("I2001", getName());
		long timeStart = System.currentTimeMillis();

		// call implemented method
		onRun();

		// hit the counter
		getStats().hitCounter("task", getName(), "count");
		long timeTask = System.currentTimeMillis() - timeStart;
		getStats().hitCounter(timeTask, "task", getName(), "time");

		// clean garbage
		System.gc();
	}

	public abstract void onRun();
}
