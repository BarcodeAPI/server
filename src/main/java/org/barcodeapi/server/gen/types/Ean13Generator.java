package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;

/**
 * Ean13Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Ean13Generator extends DefaultBarcode4JProvider {

	public Ean13Generator(CodeType codeType) {

		// Setup EAN13 generator
		super(codeType, new EAN13Bean());
	}
}
