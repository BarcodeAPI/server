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

		// time and render
		long timeStart = System.currentTimeMillis();
		byte[] image = onRender(validated);
		double time = System.currentTimeMillis() - timeStart;

		// increment counter
		counterName = "render." + getType().toString() + ".time";
		StatsCollector.getInstance().incrementCounter(counterName, time);

		// return image
		return image;
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
