package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultZXingProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;

/**
 * AztecGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class AztecGenerator extends DefaultZXingProvider {

	public AztecGenerator(CodeType codeType) {

		// Setup Aztec generator
		super(codeType, BarcodeFormat.AZTEC, new AztecWriter());
	}
}
