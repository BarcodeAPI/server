package org.barcodeapi.server.statistics;

import java.util.concurrent.ConcurrentHashMap;

public class StatsCollector {

	private static StatsCollector statsCollector;

	private ConcurrentHashMap<String, Double> hitCounters;

	public StatsCollector() {

		hitCounters = new ConcurrentHashMap<String, Double>();
	}

	public void incrementCounter(String counter) {

		incrementCounter(counter, 1d);
	}

	public void incrementCounter(String counter, Double inc) {

		Double value = hitCounters.get(counter);
		if (value == null) {

			value = 0d;
		}

		hitCounters.put(counter, value + inc);
	}

	public double getCounter(String counter) {

		return hitCounters.get(counter);
	}

	public void setCounter(String counter, Double value) {

		hitCounters.put(counter, value);
	}

	public ConcurrentHashMap<String, Double> getCounters() {

		return hitCounters;
	}

	public static synchronized StatsCollector getInstance() {

		if (statsCollector == null) {

			statsCollector = new StatsCollector();
		}
		return statsCollector;
	}

}
