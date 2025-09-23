package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;

/**
 * Ean8Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Ean8Generator extends DefaultBarcode4JProvider {

	public Ean8Generator(CodeType codeType) {

		// Setup EAN8 generator
		super(codeType, new EAN8Bean());
	}
}
