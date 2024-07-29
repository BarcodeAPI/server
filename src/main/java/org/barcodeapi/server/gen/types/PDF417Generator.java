package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * PDF417Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class PDF417Generator extends CodeGenerator {

	private PDF417Bean generator;

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public PDF417Generator() {

		generator = new PDF417Bean();
	}

	@Override
	public String onValidateRequest(String data) {
		return CodeUtils.parseControlChars(data);
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double moduleWidth = UnitConv.in2mm(5.0f / dpi);

		double qz = options.optDouble("qz", 2);

		String text = options.optString("text", "none");
		String pattern = options.optString("pattern", null);

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
			generator.setModuleWidth(moduleWidth);

			generator.setPattern(pattern);
			generator.setFontSize(12 * moduleWidth);

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
