package org.barcodeapi.server.core;

import java.util.TimerTask;

import org.barcodeapi.server.statistics.StatsCollector;

import com.mclarkdev.tools.liblog.LibLog;

public abstract class BackgroundTask extends TimerTask {

	private final StatsCollector stats;

	public BackgroundTask() {

		this.stats = StatsCollector.getInstance();
	}

	public StatsCollector getStats() {
		return stats;
	}

	@Override
	public void run() {

		// log task name
		String taskName = getClass().getName();
		taskName = taskName.substring(taskName.lastIndexOf('.') + 1);
		LibLog.clogF_("I2001", taskName);
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
