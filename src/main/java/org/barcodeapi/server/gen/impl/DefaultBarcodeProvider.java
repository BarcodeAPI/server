package org.barcodeapi.server.gen.impl;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;

public abstract class DefaultBarcodeProvider extends CodeGenerator {

	private final AbstractBarcodeBean generator;

	public DefaultBarcodeProvider(CodeType codeType, AbstractBarcodeBean bean) {
		super(codeType);

		this.generator = bean;
	}

	@Override
	public byte[] onRender(BarcodeRequest request) throws Exception {

		JSONObject options = request.getOptions();

		int dpi = options.optInt("dpi", //
				(Integer) getDefaults().getOrDefault("dpi", 150));

		int module = options.optInt("module", //
				(Integer) getDefaults().getOrDefault("module", 4));

		int qz = options.optInt("qz", //
				(Integer) getDefaults().getOrDefault("qz", 4));

		int height = options.optInt("height", //
				(Integer) getDefaults().getOrDefault("height", 22));

		int font = options.optInt("font", //
				(Integer) getDefaults().getOrDefault("font", 5));

		String text = options.optString("text", //
				(String) getDefaults().getOrDefault("text", "bottom"));

		String pattern = options.optString("pattern", //
				(String) getDefaults().getOrDefault("pattern", "_"));

		String colorFG = options.optString("fg", //
				(String) getDefaults().getOrDefault("fg", "000000"));

		String colorBG = options.optString("bg", //
				(String) getDefaults().getOrDefault("bg", "ffffff"));

		byte[] bytes;
		synchronized (generator) {

			// Set render options
			generator.doQuietZone(true);
			generator.setQuietZone(qz);
			generator.setHeight(height);
			generator.setModuleWidth((module / 10d));
			generator.setPattern(pattern);
			generator.setFontSize(font);
			generator.setMsgPosition(HumanReadablePlacement.byName(text));

			// Create the canvas object
			BarcodeCanvasProvider canvasProvider = //
					new BarcodeCanvasProvider(dpi, colorBG, colorFG);

			// Render the barcode, get as bytes
			generator.generateBarcode(canvasProvider, request.getData());
			bytes = canvasProvider.finish();
		}

		return bytes;
	}
}
