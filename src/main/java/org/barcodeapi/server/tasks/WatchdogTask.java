package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;

public class WatchdogTask extends BackgroundTask {

	private final Runtime runtime;
	private final long timeStart;

	public WatchdogTask() {
		super();

		this.runtime = Runtime.getRuntime();
		this.timeStart = System.currentTimeMillis();
	}

	@Override
	public void onRun() {

		// update uptime metric
		getStats().setCounter("system.uptime", //
				(double) (System.currentTimeMillis() - timeStart));

		// update jvm memory stats
		getStats().setCounter("jvm.memory.used", (double) (runtime.totalMemory() - runtime.freeMemory()));
		getStats().setCounter("jvm.memory.free", (double) (runtime.freeMemory()));
		getStats().setCounter("jvm.memory.total", (double) (runtime.totalMemory()));
	}
}
