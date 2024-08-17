package org.barcodeapi.server.gen.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * QRCodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class QRCodeGenerator extends CodeGenerator {

	private QRCodeWriter generator;

	public QRCodeGenerator(CodeType codeType) {
		super(codeType);

		// Setup QR-Code generator
		generator = new QRCodeWriter();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws WriterException, IOException {

		int size = options.optInt("size", 275);
		int qz = options.optInt("qz", 2);
		String correction = options.optString("correction", "M");

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hintsMap.put(EncodeHintType.ERROR_CORRECTION, //
				ErrorCorrectionLevel.valueOf(correction));
		hintsMap.put(EncodeHintType.MARGIN, qz);

		BitMatrix bitMatrix = generator.encode(//
				data, BarcodeFormat.QR_CODE, size, size, hintsMap);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

		return out.toByteArray();
	}
}
