package org.barcodeapi.server.statistics;

import org.json.JSONObject;;

public class StatsCollector {

	private static StatsCollector statsCollector;

	private final JSONObject counterCache = new JSONObject();

	public StatsCollector() {
	}

	/**
	 * Increment the value of a single counter by one.
	 * 
	 * @param name
	 */
	public void hitCounter(String... name) {
		hitCounter(1, name);
	}

	/**
	 * Increment the value of a single counter by the specified value.
	 * 
	 * @param name
	 */
	public void hitCounter(double count, String... name) {

		synchronized (counterCache) {

			JSONObject outer = counterCache;
			for (int x = 0; x < name.length - 1; x++) {

				JSONObject inner = outer.optJSONObject(name[x]);
				if (inner == null) {

					inner = new JSONObject();
					outer.put(name[x], inner);
				}
				outer = inner;
			}

			String key = name[name.length - 1];
			double current = outer.optDouble(key, 0);
			outer.put(key, current + count);
		}
	}

	/**
	 * Get a single counter by name.
	 * 
	 * @param name
	 * @return
	 */
	public double getCounter(String... name) {

		JSONObject outer = counterCache;
		for (String key : name) {

			JSONObject inner = outer.optJSONObject(key);
			if (inner == null) {

				inner = new JSONObject();
				outer.put(key, inner);
			}
			outer = inner;
		}

		return outer.getDouble("value");
	}

	/**
	 * Set the value for a counter.
	 * 
	 * @param name
	 * @param value
	 */
	public void setValue(Object value, String... name) {

		synchronized (counterCache) {

			JSONObject outer = counterCache;
			for (int x = 0; x < name.length - 1; x++) {

				JSONObject inner = outer.optJSONObject(name[x]);
				if (inner == null) {

					inner = new JSONObject();
					outer.put(name[x], inner);
				}
				outer = inner;
			}

			String key = name[name.length - 1];
			outer.put(key, value);
		}
	}

	/**
	 * Get the details of the counter cache.
	 * 
	 * @return
	 */
	public JSONObject getDetails() {

		return counterCache;
	}

	/**
	 * Get a reference to the shared statistics collector.
	 * 
	 * @return
	 */
	public static synchronized StatsCollector getInstance() {

		if (statsCollector == null) {

			statsCollector = new StatsCollector();
		}
		return statsCollector;
	}
}
