package org.barcodeapi.server.gen.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class AztecGenerator extends CodeGenerator {

	private AztecWriter codeWriter;

	public AztecGenerator() {
		super(CodeType.Aztec);

		codeWriter = new AztecWriter();
	}

	@Override
	public String onValidateRequest(String data) {

		return data;
		// return CodeUtils.parseControlChars(data);
	}

	@Override
	public byte[] onRender(String data) throws WriterException, IOException {

		int mWidth = 300;
		int mHeight = 300;

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hintsMap.put(EncodeHintType.ERROR_CORRECTION, 10);
		hintsMap.put(EncodeHintType.MARGIN, 2);

		BitMatrix bitMatrix = codeWriter.encode(//
				data, BarcodeFormat.AZTEC, mWidth, mHeight, hintsMap);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

		return out.toByteArray();
	}
}
