package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcodeProvider;
import org.krysalis.barcode4j.impl.code128.Code128Bean;

/**
 * Code128Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class DefaultGenerator extends DefaultBarcodeProvider {

	public DefaultGenerator(CodeType codeType) {

		// Setup Code128 generator
		super(codeType, new Code128Bean());
	}
}
