package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code39Generator extends CodeGenerator {

	private Code39Bean generator;

	private final int dpi = 150;

	public Code39Generator() {
		super(CodeType.Code39);

		// Setup Code39 generator
		generator = new Code39Bean();

		double moduleWidth = UnitConv.in2mm(2.5f / dpi);
		generator.setModuleWidth(moduleWidth);

		/**
		 * Set quiet zone
		 */
		generator.doQuietZone(true);
		generator.setQuietZone(10 * moduleWidth);
		generator.setVerticalQuietZone(2 * moduleWidth);

		generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);

		generator.setHeight(UnitConv.in2mm(1));
		// barcode39Bean.setBarHeight(UnitConv.in2mm(.5));

		// barcode39Bean.setFontName(name);
		// barcode39Bean.setFontSize(size);
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
