package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcodeProvider;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;

/**
 * UPCEGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class UPCEGenerator extends DefaultBarcodeProvider {

	public UPCEGenerator(CodeType codeType) {

		// Setup UPC-E generator
		super(codeType, new UPCEBean());
	}
}
