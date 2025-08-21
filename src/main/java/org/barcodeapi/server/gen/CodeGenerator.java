package org.barcodeapi.server.gen;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;

/**
 * CodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public abstract class CodeGenerator {

	private final CodeType codeType;

	public CodeGenerator(CodeType codeType) {
		this.codeType = codeType;
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
	 * Default validation does not modifications.
	 * 
	 * @param data
	 * @return
	 * @throws GenerationException
	 */
	public void onValidateRequest(BarcodeRequest data) throws GenerationException {
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
