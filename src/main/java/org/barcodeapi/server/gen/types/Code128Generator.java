package org.barcodeapi.server.gen.types;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code128Generator extends CodeGenerator {

	private Code128Bean generator;

	public Code128Generator() {

		// Setup Code128 generator
		generator = new Code128Bean();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double moduleWidth = UnitConv.in2mm(2.5f / dpi);

		double qz = options.optDouble("qz", (10 * moduleWidth));
		int height = options.optInt("height", 25);

		String text = options.optString("text", "bottom");
		String pattern = options.optString("pattern", null);

		switch (text) {

		case "bottom":
			generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
			break;

		case "top":
			generator.setMsgPosition(HumanReadablePlacement.HRP_TOP);
			break;

		case "none":
		default:
			generator.setMsgPosition(HumanReadablePlacement.HRP_NONE);
			break;
		}

		generator.doQuietZone(true);
		generator.setQuietZone(qz);
		generator.setHeight(height);
		generator.setModuleWidth(moduleWidth);

		generator.setPattern(pattern);
		generator.setFontSize(12 * moduleWidth);

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
