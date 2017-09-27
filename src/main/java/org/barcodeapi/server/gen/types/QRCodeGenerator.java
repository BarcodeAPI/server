package org.barcodeapi.server.gen.types;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.statistics.StatsCollector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator extends CodeGenerator {

	@Override
	public byte[] generateCode(String data) {

		StatsCollector.getInstance().incrementCounter("qr.render");

		String fileName = data.replace(File.separatorChar, '-');
		fileName = "cache" + File.separator + "qr" + File.separator + fileName + ".png";

		// Open output file
		try {

			int mWidth = 300;
			int mHeight = 300;

			Map<EncodeHintType, Object> hintsMap = new HashMap<>();
			hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hintsMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			hintsMap.put(EncodeHintType.MARGIN, 2);

			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);

			Path path = Paths.get(fileName);
			MatrixToImageWriter.writeToPath(bitMatrix, "png", path);

			return Files.readAllBytes(path);

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}
}
