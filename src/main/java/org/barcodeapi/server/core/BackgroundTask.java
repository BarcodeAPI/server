package org.barcodeapi.server.core;

import java.util.TimerTask;

import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.statistics.StatsCollector;

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
		Log.out(LOG.SERVER, "Running task : " + taskName);

		// hit the counter
		getStats().incrementCounter("task.count." + taskName);

		// call implemented method
		onRun();
	}

	public abstract void onRun();
}
