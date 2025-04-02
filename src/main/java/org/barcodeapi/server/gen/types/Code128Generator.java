package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.code128.Code128Bean;

/**
 * Code128Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Code128Generator extends DefaultBarcode4JProvider {

	public Code128Generator(CodeType codeType) {

		// Setup Code128 generator
		super(codeType, new Code128Bean());
	}
}
