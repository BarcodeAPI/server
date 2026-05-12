package org.barcodeapi.server.gen.types;

import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.impl.DefaultZXingProvider;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * QRCodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class QRCodeGenerator extends DefaultZXingProvider {

	public QRCodeGenerator(CodeType codeType) {

		// Setup QR generator
		super(codeType, BarcodeFormat.QR_CODE, new QRCodeWriter());
	}

	@Override
	public byte[] onRender(BarcodeRequest request) throws Exception {

		JSONObject options = request.getOptions();

		HashMap<String, Object> defaults = //
				request.getType().getDefaults();

		int size = options.optInt("size", //
				(Integer) defaults.getOrDefault("size", 275));

		int qz = options.optInt("qz", //
				(Integer) defaults.getOrDefault("qz", 4));

		String correction = options.optString("correction", //
				(String) defaults.getOrDefault("correction", "m"));

		ErrorCorrectionLevel eccLevel = //
				ErrorCorrectionLevel.valueOf(//
						correction.toUpperCase());

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		hintsMap.put(EncodeHintType.ERROR_CORRECTION, eccLevel);
		hintsMap.put(EncodeHintType.MARGIN, qz);

		BitMatrix bitMatrix;
		synchronized (generator) {

			bitMatrix = generator.encode(//
					request.getData(), format, size, size, hintsMap);
		}

		return toImage(bitMatrix);
	}
}
