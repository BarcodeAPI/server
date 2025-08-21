package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;

/**
 * PDF417Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class PDF417Generator extends DefaultBarcode4JProvider {

	public PDF417Generator(CodeType codeType) {

		// Setup PDF417 generator
		super(codeType, new PDF417Bean());
	}
}
