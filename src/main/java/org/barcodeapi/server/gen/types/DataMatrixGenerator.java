package org.barcodeapi.server.gen.types;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;

/**
 * DataMatrixGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean generator;

	public DataMatrixGenerator() {

		// Setup DataMatrix generator
		generator = new DataMatrixBean();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double scale = options.optDouble("scale", 1.5);
		double qz = options.optDouble("qz", 3);

		boolean square = options.optBoolean("square", true);

		if (square) {
			generator.setShape(SymbolShapeHint.FORCE_SQUARE);
		} else {
			generator.setShape(SymbolShapeHint.FORCE_NONE);
		}

		generator.doQuietZone(true);
		generator.setQuietZone(qz);
		generator.setModuleWidth(scale);

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
