package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBCBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class RoyalMailGenerator extends CodeGenerator {

	private RoyalMailCBCBean generator;

	public RoyalMailGenerator() {
		super(CodeType.RoyalMail);

		// Setup Code39 generator
		generator = new RoyalMailCBCBean();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		// double moduleWidth = UnitConv.in2mm(2.5f / dpi);

		// int qz = options.optInt("qz", 4);
		// int height = options.optInt("height", 25);

		// String text = options.optString("text", "none");

		synchronized (generator) {

			// switch (text) {

			// case "bottom":
			// generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
			// break;

			// case "top":
			// generator.setMsgPosition(HumanReadablePlacement.HRP_TOP);
			// break;

			// case "none":
			// default:
			// generator.setMsgPosition(HumanReadablePlacement.HRP_NONE);
			// break;
			// }

			// generator.doQuietZone(true);
			// generator.setQuietZone(qz);
			// generator.setVerticalQuietZone(2 * moduleWidth);
			// generator.setHeight(height);
			// generator.setModuleWidth(moduleWidth);

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
