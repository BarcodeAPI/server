package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultBarcode4JProvider;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;

/**
 * DataMatrixGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class DataMatrixGenerator extends DefaultBarcode4JProvider {

	public DataMatrixGenerator(CodeType codeType) {
		super(codeType, initBean());
	}

	private static DataMatrixBean initBean() {
		DataMatrixBean bean = new DataMatrixBean();
		bean.setShape(SymbolShapeHint.FORCE_SQUARE);
		return bean;
	}
}
