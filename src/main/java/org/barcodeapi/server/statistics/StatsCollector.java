package org.barcodeapi.server.statistics;

import java.util.concurrent.ConcurrentHashMap;

public class StatsCollector {

	private static StatsCollector statsCollector;

	private ConcurrentHashMap<String, Long> hitCounters;

	public StatsCollector() {

		hitCounters = new ConcurrentHashMap<String, Long>();
	}

	public void incrementCounter(String counter) {

		Long value = hitCounters.get(counter);
		if (value == null) {

			value = 0l;
		}

		hitCounters.put(counter, value + 1);
	}

	public double getCounter(String counter) {

		return hitCounters.get(counter);
	}

	public ConcurrentHashMap<String, Long> getCounters() {

		return hitCounters;
	}

	public static synchronized StatsCollector getInstance() {

		if (statsCollector == null) {

			statsCollector = new StatsCollector();
		}
		return statsCollector;
	}
}
