package org.barcodeapi.server.gen.types;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator extends CodeGenerator {

	private QRCodeWriter codeWriter;

	public QRCodeGenerator() {
		super(CodeType.QRCode);

		codeWriter = new QRCodeWriter();
	}

	@Override
	public void onValidateRequest(String data) {

		/**
		 * Validate against Code128 specifications.
		 * 
		 * https://en.wikipedia.org/wiki/Code_128#Bar_code_widths
		 * 
		 */
		if (!data.matches("[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+")) {

			throw new IllegalArgumentException("Invalid QRCode format.");
		}

		// Allow max of 50 characters
		if (data.length() > 50) {

			throw new IllegalArgumentException("Too many characters.");
		}
	}

	@Override
	public byte[] onRender(String data) {

		try {

			int mWidth = 300;
			int mHeight = 300;

			Map<EncodeHintType, Object> hintsMap = new HashMap<>();
			hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hintsMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			hintsMap.put(EncodeHintType.MARGIN, 2);

			BitMatrix bitMatrix = codeWriter.encode(//
					data, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

			return out.toByteArray();

		} catch (Exception e) {

			e.printStackTrace();
			throw new IllegalStateException("An error has occured.");
		}
	}
}
