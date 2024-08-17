package org.barcodeapi.server.gen.impl;

import java.util.HashMap;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;

public abstract class Barcode4JProvider extends CodeGenerator {

	private final AbstractBarcodeBean generator;

	private final HashMap<String, Object> defaults;

	public Barcode4JProvider(CodeType codeType, AbstractBarcodeBean bean) {
		super(codeType);

		this.generator = bean;
		this.defaults = new HashMap<>();

		// Determine default values
		JSONObject options = getType().getOptions();
		for (String optionName : options.keySet()) {
			defaults.put(optionName, //
					options.getJSONObject(optionName).get("default"));
		}
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws Exception {

		int dpi = options.optInt("dpi", //
				(Integer) defaults.getOrDefault("dpi", 150));

		int module = options.optInt("module", //
				(Integer) defaults.getOrDefault("module", 4));

		int qz = options.optInt("qz", //
				(Integer) defaults.getOrDefault("qz", 4));

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
			generator.setModuleWidth((module / 10d));
			generator.setPattern(pattern);
			generator.setFontSize(font);

			BarcodeCanvasProvider canvasProvider = //
					new BarcodeCanvasProvider(dpi, colorBG, colorFG);

			generator.generateBarcode(canvasProvider, data);
			bytes = canvasProvider.finish();
		}

		return bytes;
	}
}
