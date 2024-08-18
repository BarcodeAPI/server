package org.barcodeapi.server.core;

import java.util.TimerTask;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * BackgroundTask.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public abstract class BackgroundTask extends TimerTask {

	private final LibMetrics stats = LibMetrics.instance();

	private final String name;

	public BackgroundTask() {

		String clazz = getClass().getName();
		name = clazz.substring(clazz.lastIndexOf('.') + 1);
	}

	/**
	 * Returns an instance of the stats collector.
	 * 
	 * @return an instance of the stats collector
	 */
	protected LibMetrics getStats() {
		return stats;
	}

	/**
	 * Returns the name of the background task.
	 * 
	 * @return the name of the background task
	 */
	public String getName() {
		return name;
	}

	@Override
	public void run() {

		// Log the task name
		LibLog._clogF("I2001", getName());
		long timeStart = System.currentTimeMillis();

		// Call implemented method
		this.onRun();

		// Hit the counter
		getStats().hitCounter("task", getName(), "count");
		long timeTask = System.currentTimeMillis() - timeStart;
		getStats().hitCounter(timeTask, "task", getName(), "time");

		// Clean garbage
		System.gc();
	}

	/**
	 * The implemented logic to run on execution of the BackgroundTask.
	 */
	public abstract void onRun();
}
