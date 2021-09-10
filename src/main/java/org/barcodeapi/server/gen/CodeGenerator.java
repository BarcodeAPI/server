package org.barcodeapi.server.gen;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;
import org.json.JSONObject;

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

		String type = getType().toString();

		// validate code format
		if (!data.matches(getType().getFormatPattern())) {

			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Invalid data for selected code type"));
		}

		// any additional generator validations
		String validated = onValidateRequest(data);
		try {
			// start timer and render
			long timeStart = System.currentTimeMillis();
			byte[] img = onRender(validated, options);
			double time = System.currentTimeMillis() - timeStart;

			Log.out(LOG.BARCODE, "" + //
					"Rendered [ " + getType().toString() + " ] " + //
					"with [ " + data + " ] " + //
					"size [ " + img.length + "B ] " + //
					"in [ " + time + "ms ] ");

			return img;
		} catch (Exception e) {
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
