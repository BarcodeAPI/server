package org.barcodeapi.server.gen.types;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.impl.DefaultZXingProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * QRCodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class QRCodeGenerator extends DefaultZXingProvider {

	public QRCodeGenerator(CodeType codeType) {

		// Setup QR generator
		super(codeType, BarcodeFormat.QR_CODE, new QRCodeWriter());
	}
}
