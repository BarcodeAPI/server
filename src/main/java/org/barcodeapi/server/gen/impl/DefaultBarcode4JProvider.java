package org.barcodeapi.server.gen.impl;

import java.util.HashMap;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;

public abstract class DefaultBarcode4JProvider extends CodeGenerator {

	private final AbstractBarcodeBean generator;

	public DefaultBarcode4JProvider(CodeType codeType, AbstractBarcodeBean bean) {
		super(codeType);

		this.generator = bean;
	}

	protected AbstractBarcodeBean getBean() {

		return this.generator;
	}

	@Override
	public byte[] onRender(BarcodeRequest request) throws Exception {

		JSONObject options = request.getOptions();

		HashMap<String, Object> defaults = //
				request.getType().getDefaults();

		int dpi = options.optInt("dpi", //
				(Integer) defaults.getOrDefault("dpi", 100));

		int module = options.optInt("module", //
				(Integer) defaults.getOrDefault("module", 10));

		int qz = options.optInt("qz", //
				(Integer) defaults.getOrDefault("qz", 3));

		int height = options.optInt("height", //
				(Integer) defaults.getOrDefault("height", 22));

		int font = options.optInt("font", //
				(Integer) defaults.getOrDefault("font", 5));

		String text = options.optString("text", //
				(String) defaults.getOrDefault("text", "bottom"));

		String pattern = options.optString("pattern", //
				(String) defaults.getOrDefault("pattern", "_"));

		String colorFG = options.optString("fg", //
				(String) defaults.getOrDefault("fg", "000000"));

		String colorBG = options.optString("bg", //
				(String) defaults.getOrDefault("bg", "ffffff"));

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
