package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;

/**
 * UPCEGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class UPCEGenerator extends DefaultBarcode4JProvider {

	public UPCEGenerator(CodeType codeType) {

		// Setup UPC-E generator
		super(codeType, new UPCEBean());
	}
}
