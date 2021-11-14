package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code128Generator extends CodeGenerator {

	private Code128Bean generator;

	public Code128Generator() {
		super(CodeType.Code128);

		// Setup Code128 generator
		generator = new Code128Bean();
	}

	@Override
	public String onValidateRequest(String data) {

		return CodeUtils.parseControlChars(data);
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double moduleWidth = UnitConv.in2mm(2.5f / dpi);

		int qz = options.optInt("qz", (int) (10 * moduleWidth));
		int height = options.optInt("height", 25);

		String text = options.optString("text", "bottom");

		synchronized (generator) {

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
