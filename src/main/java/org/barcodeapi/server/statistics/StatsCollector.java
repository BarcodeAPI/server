package org.barcodeapi.server.statistics;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;;

public class StatsCollector {

	private static final String _RUNTIME_ID = UUID.randomUUID().toString();

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

	public JSONObject dumpJSON() {

		JSONObject output = new JSONObject()//
				.put("runtimeId", _RUNTIME_ID);

		for (Map.Entry<String, Double> entry : hitCounters.entrySet()) {
			output.put(entry.getKey(), entry.getValue());
		}

		return output;
	}

	public static synchronized StatsCollector getInstance() {

		if (statsCollector == null) {

			statsCollector = new StatsCollector();
		}
		return statsCollector;
	}

}
