package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBCBean;

/**
 * RoyalMailGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class RoyalMailGenerator extends DefaultBarcode4JProvider {

	public RoyalMailGenerator(CodeType codeType) {

		// Setup RoyalMail generator
		super(codeType, new RoyalMailCBCBean());
	}
}
