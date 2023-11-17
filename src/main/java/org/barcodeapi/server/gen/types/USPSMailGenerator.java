package org.barcodeapi.server.gen.types;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class USPSMailGenerator extends CodeGenerator {

	private USPSIntelligentMailBean generator;

	public USPSMailGenerator() {
		super(CodeType.USPSMail);

		// Setup Code39 generator
		generator = new USPSIntelligentMailBean();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);

		synchronized (generator) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BarcodeCanvasProvider canvasProvider = new BarcodeCanvasProvider(out, dpi);
			canvasProvider.setColors(//
					Color.decode("0x" + options.optString("bg", "ffffff")), //
					Color.decode("0x" + options.optString("fg", "000000")));

			generator.generateBarcode(canvasProvider, data);
			canvasProvider.finish();
			out.close();

			return out.toByteArray();
		}
	}
}
