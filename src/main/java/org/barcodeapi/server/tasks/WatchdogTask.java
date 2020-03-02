package org.barcodeapi.server.tasks;

import java.util.TimerTask;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;
import org.barcodeapi.server.statistics.StatsCollector;

public class WatchdogTask extends TimerTask {

	private final long timeStart;

	public WatchdogTask() {

		timeStart = System.currentTimeMillis();
	}

	@Override
	public void run() {

		Log.out(LOG.SERVER, "Watchdog Task");
		double timeUp = System.currentTimeMillis() - timeStart;
		StatsCollector.getInstance().setCounter("system.uptime", timeUp);
	}
}
