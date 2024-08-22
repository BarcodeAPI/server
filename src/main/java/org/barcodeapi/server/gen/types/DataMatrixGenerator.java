package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcodeProvider;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;

/**
 * DataMatrixGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class DataMatrixGenerator extends DefaultBarcodeProvider {

	public DataMatrixGenerator(CodeType codeType) {

		// Setup DataMatrix generator
		super(codeType, new DataMatrixBean());
	}
}
