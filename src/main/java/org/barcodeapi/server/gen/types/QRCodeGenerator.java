package org.barcodeapi.server.gen.types;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public QRCodeGenerator() {
		super(CodeType.QRCode);

	}

	@Override
	public boolean onRender(String data, File outputFile) {

		try {

			int mWidth = 300;
			int mHeight = 300;

			Map<EncodeHintType, Object> hintsMap = new HashMap<>();
			hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hintsMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			hintsMap.put(EncodeHintType.MARGIN, 2);

			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);

			Path path = Paths.get(outputFile.getAbsolutePath());
			MatrixToImageWriter.writeToPath(bitMatrix, "png", path);

			return true;

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}
}
