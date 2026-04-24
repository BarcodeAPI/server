package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultZXingProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.oned.Code93Writer;

/**
 * Code93Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class Code93Generator extends DefaultZXingProvider {

	public Code93Generator(CodeType codeType) {

		// Setup QR generator
		super(codeType, BarcodeFormat.CODE_93, new Code93Writer());
	}
}
