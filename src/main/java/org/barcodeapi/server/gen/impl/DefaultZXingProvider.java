package org.barcodeapi.server.gen.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * DefaultZXingProvider.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public abstract class DefaultZXingProvider extends CodeGenerator {

	protected final Writer generator;

	protected final BarcodeFormat format;

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

		BitMatrix bitMatrix;
		synchronized (generator) {

			bitMatrix = generator.encode(//
					request.getData(), format, size, size, null);
		}

		return toImage(bitMatrix);
	}

	protected byte[] toImage(BitMatrix bitMatrix) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

		return out.toByteArray();
	}
}
