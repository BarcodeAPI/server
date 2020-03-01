package org.barcodeapi.server.gen;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
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
	public byte[] getCode(String data) throws GenerationException {

		// validate code format
		if (!data.matches(getType().getFormatPattern())) {

			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Invalid data for selected code type"));
		}

		// any additional generator validations
		String validated = onValidateRequest(data);

		// update global and engine counters
		StatsCollector.getInstance().incrementCounter("render.total.count");
		String counterName = "render." + getType().toString() + ".count";
		StatsCollector.getInstance().incrementCounter(counterName);

		try {

			// start timer and render
			long timeStart = System.currentTimeMillis();
			byte[] img = onRender(validated);
			double time = System.currentTimeMillis() - timeStart;

			// update global and engine counters
			StatsCollector.getInstance().incrementCounter("render.total.time", time);
			counterName = "render." + getType().toString() + ".time";
			StatsCollector.getInstance().incrementCounter(counterName, time);

			return img;
		} catch (Exception e) {

			// update global and engine counters
			StatsCollector.getInstance().incrementCounter("render.total.fail");
			counterName = "render." + getType().toString() + ".fail";
			StatsCollector.getInstance().incrementCounter(counterName);

			throw new GenerationException(ExceptionType.FAILED, e);
		}
	}

	/**
	 * Default validation does not modifications.
	 * 
	 * @param data
	 * @return
	 * @throws GenerationException
	 */
	public String onValidateRequest(String data) throws GenerationException {

		return data;
	}

	/**
	 * Implemented by the specific generator.
	 * 
	 * @param data
	 * @return
	 */
	public abstract byte[] onRender(String data) throws Exception;
}
