package org.barcodeapi.server.gen;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

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
	public byte[] getCode(String data, JSONObject options) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		String type = getType().toString();

		// validate code format
		if (!data.matches(getType().getFormatPattern())) {

			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Invalid data for selected code type"));
		}

		// any additional generator validations
		String validated = onValidateRequest(data);

		// update global and engine counters
		LibMetrics.instance().hitCounter("render", "count");
		LibMetrics.instance().hitCounter("render", "type", type, "count");

		try {

			// start timer and render
			long timeStart = System.currentTimeMillis();
			byte[] img = onRender(validated, options);
			double time = System.currentTimeMillis() - timeStart;

			// update global and engine counters
			LibMetrics.instance().hitCounter(time, "render", "time");
			LibMetrics.instance().hitCounter(time, "render", "type", type, "time");

			// log the render
			LibLog.clogF("barcode", "I0601", getType().toString(), data, img.length, time);

			return img;
		} catch (Exception e) {

			// update global and engine counters
			LibMetrics.instance().hitCounter("render", "fail");
			LibMetrics.instance().hitCounter("render", "type", type, "fail");

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
	public abstract byte[] onRender(String data, JSONObject options) throws Exception;
}
