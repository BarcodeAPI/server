package org.barcodeapi.server.gen;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.statistics.StatsCollector;

public abstract class CodeGenerator {

	private final CodeType codeType;

	/**
	 * Initialize a new generator of the defined type.
	 * 
	 * @param type
	 */
	public CodeGenerator(CodeType type) {

		this.codeType = type;

		BarcodeCache.getInstance().createCache(type);
	}

	/**
	 * Get the defined code type.
	 * 
	 * @return
	 */
	public CodeType getType() {

		return codeType;
	}

	/**
	 * Get the raw bytes of a PNG for the given data string.
	 * 
	 * @param data
	 * @return
	 */
	public byte[] getCode(String data) {

		// validate code format
		if (!data.matches(getType().getFormatPattern())) {

			throw new IllegalArgumentException("Invalid data for selected code type");
		}

		// any additional generator validations
		String validated = onValidateRequest(data);

		// increment counter
		String counterName = "render." + getType().toString() + ".hit";
		StatsCollector.getInstance().incrementCounter(counterName);

		// start timer
		long timeStart = System.currentTimeMillis();

		try {

			// render image
			byte[] img = onRender(validated);

			// time and update counter
			double time = System.currentTimeMillis() - timeStart;
			counterName = "render." + getType().toString() + ".time";
			StatsCollector.getInstance().incrementCounter(counterName, time);

			// return image
			return img;
		} catch (Exception e) {

			// hit fail counter
			counterName = "render." + getType().toString() + ".fail";
			StatsCollector.getInstance().incrementCounter(counterName);

			// return null
			return null;
		}
	}

	/**
	 * Default validation does not modifications.
	 * 
	 * @param data
	 * @return
	 */
	public String onValidateRequest(String data) {

		return data;
	}

	/**
	 * Implemented by the specific generator.
	 * 
	 * @param data
	 * @return
	 */
	public abstract byte[] onRender(String data);
}
