package org.barcodeapi.server.tasks;

import java.lang.Thread.State;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Set;

import org.barcodeapi.core.ServerRuntime;
import org.barcodeapi.server.core.BackgroundTask;

public class WatchdogTask extends BackgroundTask {

	private final Runtime runtime;

	public WatchdogTask() {
		super();

		this.runtime = Runtime.getRuntime();
	}

	@Override
	public void onRun() {

		// update system runtime statistics
		getStats().setValue(System.currentTimeMillis(), "system", "time", "now");
		getStats().setValue(ServerRuntime.getTimeRunning(), "system", "time", "running");

		// update jvm memory statistics
		double memUsed = (double) (runtime.totalMemory() - runtime.freeMemory());
		getStats().setValue(memUsed, "system", "jvm", "memory", "used");

		double memFree = (double) (runtime.freeMemory());
		getStats().setValue(memFree, "system", "jvm", "memory", "free");

		double memTotal = (double) (runtime.totalMemory());
		getStats().setValue(memTotal, "system", "jvm", "memory", "total");

		double memMax = (double) (runtime.maxMemory());
		getStats().setValue(memMax, "system", "jvm", "memory", "max");

		// update jvm gc statistics
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {

			String name = gc.getName().replace(" ", "_");
			getStats().setValue(gc.getCollectionCount(), "system", "jvm", "gc", name, "count");
			getStats().setValue(gc.getCollectionTime(), "system", "jvm", "gc", name, "time");
		}

		// update jvm thread statistics
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);
		getStats().setValue(threads.length, "system", "jvm", "threads", "count");

		// get state of each thread
		HashMap<State, Integer> threadStates = new HashMap<>();
		for (Thread t : threads) {
			int cur = threadStates.getOrDefault(t.getState(), 0);
			threadStates.put(t.getState(), cur + 1);
		}

		// update thread state counters
		for (State state : threadStates.keySet()) {
			getStats().setValue(threadStates.get(state), "system", "jvm", "threads", "state", state.toString());
		}
	}
}
