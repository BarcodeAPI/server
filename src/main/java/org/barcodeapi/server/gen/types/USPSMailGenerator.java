package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;

/**
 * USPSMailGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class USPSMailGenerator extends DefaultBarcode4JProvider {

	public USPSMailGenerator(CodeType codeType) {

		// Setup USPS-Mail generator
		super(codeType, new USPSIntelligentMailBean());
	}
}
