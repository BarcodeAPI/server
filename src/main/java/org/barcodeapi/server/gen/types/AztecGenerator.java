package org.barcodeapi.server.gen.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class AztecGenerator extends CodeGenerator {

	private AztecWriter generator;

	public AztecGenerator() {
		super(CodeType.Aztec);

		generator = new AztecWriter();
	}

	@Override
	public String onValidateRequest(String data) {

		return CodeUtils.parseControlChars(data);
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws WriterException, IOException {

		int size = options.optInt("size", 300);
		int correction = options.optInt("correction", 4);
		double qz = options.optDouble("qz", 2);

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		hintsMap.put(EncodeHintType.ERROR_CORRECTION, correction);
		hintsMap.put(EncodeHintType.MARGIN, qz);

		BitMatrix bitMatrix = generator.encode(//
				data, BarcodeFormat.AZTEC, size, size, hintsMap);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

		return out.toByteArray();
	}
}
