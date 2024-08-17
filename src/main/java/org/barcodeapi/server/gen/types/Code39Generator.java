package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.Barcode4JProvider;
import org.krysalis.barcode4j.impl.code39.Code39Bean;

/**
 * Code39Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Code39Generator extends Barcode4JProvider {

	public Code39Generator(CodeType codeType) {

		// Setup Code39 generator
		super(codeType, new Code39Bean());
	}
}
