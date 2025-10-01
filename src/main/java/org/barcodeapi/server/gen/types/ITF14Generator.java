package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.int2of5.ITF14Bean;

/**
 * ITF14Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ITF14Generator extends DefaultBarcode4JProvider {

	public ITF14Generator(CodeType codeType) {

		// Setup ITF14 generator
		super(codeType, new ITF14Bean());
	}
}
