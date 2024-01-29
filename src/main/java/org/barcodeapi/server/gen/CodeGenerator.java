package org.barcodeapi.server.gen;

import org.barcodeapi.server.core.GenerationException;
import org.json.JSONObject;

public abstract class CodeGenerator {

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
	 * Called when a barcode should be rendered.
	 * 
	 * @param data
	 * @return
	 */
	public abstract byte[] onRender(String data, JSONObject options) throws Exception;
}
