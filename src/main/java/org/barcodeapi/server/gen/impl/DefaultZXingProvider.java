package org.barcodeapi.server.gen.impl;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public abstract class DefaultZXingProvider extends CodeGenerator {

	private final Writer generator;

	private final BarcodeFormat format;

	public DefaultZXingProvider(CodeType codeType, BarcodeFormat format, Writer bean) {
		super(codeType);

		this.format = format;
		this.generator = bean;
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

		// String correction = options.optString("correction", //
		// (String) defaults.getOrDefault("correction", "M"));

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		// hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
		// hintsMap.put(EncodeHintType.ERROR_CORRECTION, //
		// ErrorCorrectionLevel.valueOf(correction));
		hintsMap.put(EncodeHintType.MARGIN, qz);

		synchronized (generator) {

			BitMatrix bitMatrix = generator.encode(//
					request.getData(), format, size, size, hintsMap);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

			return out.toByteArray();
		}
	}
}
