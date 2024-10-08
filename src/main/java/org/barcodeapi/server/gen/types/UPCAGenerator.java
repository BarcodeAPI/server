package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcodeProvider;
import org.krysalis.barcode4j.impl.upcean.UPCABean;

/**
 * UPCAGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class UPCAGenerator extends DefaultBarcodeProvider {

	public UPCAGenerator(CodeType codeType) {

		// Setup UPC-A generator
		super(codeType, new UPCABean());
	}
}
