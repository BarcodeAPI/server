package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class PDF417Generator extends CodeGenerator {

	private PDF417Bean generator;

	private final int dpi = 200;

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public PDF417Generator() {
		super(CodeType.PDF417);

		generator = new PDF417Bean();

		// configure barcode generator
		generator.setQuietZone(2);
		generator.doQuietZone(true);
		generator.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
	}

	@Override
	public String onValidateRequest(String data) {

		return data;
	}

	@Override
	public synchronized byte[] onRender(String data, JSONObject options) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
				out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		generator.generateBarcode(canvasProvider, data);

		canvasProvider.getBufferedImage();
		canvasProvider.finish();
		out.close();

		return out.toByteArray();
	}
}
