package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.statistics.StatsCollector;

public class WatchdogTask extends BackgroundTask {

	private final long timeStart;

	public WatchdogTask() {

		timeStart = System.currentTimeMillis();
	}

	@Override
	public void onRun() {

		double timeUp = System.currentTimeMillis() - timeStart;
		StatsCollector.getInstance().setCounter("system.uptime", timeUp);
	}
}
