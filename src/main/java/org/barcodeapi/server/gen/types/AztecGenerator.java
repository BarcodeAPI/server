package org.barcodeapi.server.gen.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * AztecGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class AztecGenerator extends CodeGenerator {

	private AztecWriter generator;

	public AztecGenerator(CodeType codeType) {
		super(codeType);

		// Setup Aztec generator
		generator = new AztecWriter();
	}

	@Override
	public byte[] onRender(BarcodeRequest request) throws WriterException, IOException {

		JSONObject options = request.getOptions();

		int size = options.optInt("size", //
				(Integer) getDefaults().getOrDefault("size", 275));

		double qz = options.optDouble("qz", //
				(Integer) getDefaults().getOrDefault("qz", 4));

		int correction = options.optInt("correction", //
				(Integer) getDefaults().getOrDefault("correction", 33));

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		hintsMap.put(EncodeHintType.ERROR_CORRECTION, correction);
		hintsMap.put(EncodeHintType.MARGIN, qz);

		BitMatrix bitMatrix = generator.encode(//
				request.getData(), BarcodeFormat.AZTEC, size, size, hintsMap);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

		return out.toByteArray();
	}
}
