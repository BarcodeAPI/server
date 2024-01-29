package org.barcodeapi.server.gen.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator extends CodeGenerator {

	private QRCodeWriter generator;

	public QRCodeGenerator() {

		generator = new QRCodeWriter();
	}

	@Override
	public String onValidateRequest(String data) {

		return CodeUtils.parseControlChars(data);
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws WriterException, IOException {

		int size = options.optInt("size", 300);

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hintsMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hintsMap.put(EncodeHintType.MARGIN, 2);

		BitMatrix bitMatrix = generator.encode(//
				data, BarcodeFormat.QR_CODE, size, size, hintsMap);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

		return out.toByteArray();
	}
}
