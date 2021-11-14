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

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public PDF417Generator() {
		super(CodeType.PDF417);

		generator = new PDF417Bean();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double moduleWidth = UnitConv.in2mm(5.0f / dpi);

		int qz = options.optInt("qz", 2);

		synchronized (generator) {

			generator.doQuietZone(true);
			generator.setQuietZone(qz);
			generator.setModuleWidth(moduleWidth);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			generator.generateBarcode(canvasProvider, data);
			canvasProvider.finish();
			out.close();

			return out.toByteArray();
		}
	}
}
