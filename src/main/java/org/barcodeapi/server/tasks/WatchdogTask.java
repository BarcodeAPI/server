package org.barcodeapi.server.tasks;

import java.lang.Thread.State;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Set;

import org.barcodeapi.core.ServerRuntime;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.gen.CodeGenerator;

import com.mclarkdev.tools.libobjectpooler.LibObjectPooler;

/**
 * WatchdogTask.java
 * 
 * A background task which monitors the health state of the application.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class WatchdogTask extends BackgroundTask {

	private final Runtime runtime = Runtime.getRuntime();

	public WatchdogTask() {
		super();
	}

	@Override
	public void onRun() {

		updateRunningTime();

		updateJVMMemoryStatistics();

		updateJVMGCStatistics();

		updateJVMThreadStateCounters();

		updateGeneratorPoolStatistics();

		updateCacheStatistics();
	}

	private void updateRunningTime() {

		// Update system runtime statistics
		getStats().setValue(System.currentTimeMillis(), "system", "time", "now");
		getStats().setValue(ServerRuntime.getTimeRunning(), "system", "time", "running");
	}

	private void updateJVMMemoryStatistics() {

		// Get and update JVM memory used
		double memUsed = (double) (runtime.totalMemory() - runtime.freeMemory());
		getStats().setValue(memUsed, "system", "jvm", "memory", "used");

		// Get and update JVM memory free
		double memFree = (double) (runtime.freeMemory());
		getStats().setValue(memFree, "system", "jvm", "memory", "free");

		// Get and update JVM memory total
		double memTotal = (double) (runtime.totalMemory());
		getStats().setValue(memTotal, "system", "jvm", "memory", "total");

		// Get and update JVM memory max
		double memMax = (double) (runtime.maxMemory());
		getStats().setValue(memMax, "system", "jvm", "memory", "max");
	}

	private void updateJVMGCStatistics() {

		// Update JVM garbage collection statistics
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {

			String name = gc.getName().replace(" ", "_");
			getStats().setValue(gc.getCollectionCount(), "system", "jvm", "gc", name, "count");
			getStats().setValue(gc.getCollectionTime(), "system", "jvm", "gc", name, "time");
		}
	}

	private void updateJVMThreadStateCounters() {

		// Update JVM thread statistics
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);
		getStats().setValue(threads.length, "system", "jvm", "threads", "count");

		// Get the state of each thread
		HashMap<State, Integer> threadStates = new HashMap<>();
		for (Thread thread : threads) {
			int current = threadStates.getOrDefault(thread.getState(), 0);
			threadStates.put(thread.getState(), current + 1);
		}

		// Update thread state counters
		for (State state : threadStates.keySet()) {
			getStats().setValue(threadStates.get(state), "system", "jvm", "threads", "state", state.toString());
		}
	}

	private void updateGeneratorPoolStatistics() {

		// Loop each type of barcode generator
		for (String type : CodeTypes.inst().getTypes()) {

			// Access generator pool
			LibObjectPooler<CodeGenerator> pool = //
					CodeGenerators.getInstance().getGeneratorPool(type);

			// Update counters for each generator pool
			getStats().setValue(pool.getMaxAge(), "generators", type, "pool", "maxAge");
			getStats().setValue(pool.getMaxIdle(), "generators", type, "pool", "maxIdle");
			getStats().setValue(pool.getMaxLockCount(), "generators", type, "pool", "maxLockCount");
			getStats().setValue(pool.getMaxPoolSize(), "generators", type, "pool", "maxPoolSize");
			getStats().setValue(pool.getNumLocked(), "generators", type, "pool", "numLocked");
			getStats().setValue(pool.getPoolSize(), "generators", type, "pool", "size");
		}
	}

	private void updateCacheStatistics() {

		// Update size counters for all caches
		String[] appCaches = ObjectCache.getCacheNames();

		for (String cache : appCaches) {
			getStats().setValue(ObjectCache//
					.getCache(cache).count(), "cache", cache, "size");
		}
	}
}
