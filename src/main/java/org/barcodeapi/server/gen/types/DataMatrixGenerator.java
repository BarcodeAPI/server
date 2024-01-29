package org.barcodeapi.server.gen.types;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;

public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean generator;

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public DataMatrixGenerator() {

		generator = new DataMatrixBean();
	}

	@Override
	public String onValidateRequest(String data) {

		return CodeUtils.parseControlChars(data);
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 200);
		double scale = options.optDouble("scale", 1.5);
		double qz = options.optDouble("qz", (scale * 2));

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
