package org.barcodeapi.server.gen;

import java.util.HashMap;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.json.JSONObject;

/**
 * CodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public abstract class CodeGenerator {

	private final CodeType codeType;

	private final HashMap<String, Object> defaults;

	public CodeGenerator(CodeType codeType) {
		this.codeType = codeType;

		this.defaults = new HashMap<>();

		// Determine default values
		JSONObject options = getType().getOptions();
		for (String optionName : options.keySet()) {
			defaults.put(optionName, //
					options.getJSONObject(optionName).get("default"));
		}
	}

	/**
	 * Returns the CodeType associated with the generator.
	 * 
	 * @return associated CodeType
	 */
	public CodeType getType() {
		return codeType;
	}

	/**
	 * Returns a map of default config values.
	 * 
	 * @return map of defaults
	 */
	public HashMap<String, Object> getDefaults() {
		return defaults;
	}

	/**
	 * Default validation does not modifications.
	 * 
	 * @param data
	 * @return
	 * @throws GenerationException
	 */
	public void onValidateRequest(String data) throws GenerationException {
	}

	/**
	 * Implemented by the specific generator.
	 * 
	 * Called when a barcode should be rendered.
	 * 
	 * @param data
	 * @return
	 */
	public abstract byte[] onRender(BarcodeRequest request) throws Exception;
}
