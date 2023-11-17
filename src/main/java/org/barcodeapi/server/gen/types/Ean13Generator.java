package org.barcodeapi.server.gen.types;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.tools.UnitConv;

public class Ean13Generator extends CodeGenerator {

	private EAN13Bean generator;

	public Ean13Generator() {
		super(CodeType.EAN13);

		generator = new EAN13Bean();
	}

	@Override
	public String onValidateRequest(String data) throws GenerationException {

		if (data.length() == 12) {

			return data;
		}

		int checksum = CodeUtils.calculateEanChecksum(data, 13);
		String provided = data.substring(data.length() - 1);

		if (!Integer.toString(checksum).equals(provided)) {

			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Expected checksum : " + checksum));
		}

		return data;
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double moduleWidth = UnitConv.in2mm(2.5f / dpi);

		double qz = options.optDouble("qz", 4);
		int height = options.optInt("height", 25);

		String text = options.optString("text", "bottom");
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
}
