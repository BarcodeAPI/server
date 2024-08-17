package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.Barcode4JProvider;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;

/**
 * CodabarGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CodabarGenerator extends Barcode4JProvider {

	public CodabarGenerator(CodeType codeType) {

		// Setup Codabar generator
		super(codeType, new CodabarBean());
	}
}
