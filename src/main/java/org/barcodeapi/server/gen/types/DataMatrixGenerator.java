package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean generator;

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public DataMatrixGenerator() {
		super(CodeType.DataMatrix);

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
		int qz = options.optInt("qz", (int) (scale * 2));

		boolean square = options.optBoolean("square", true);

		synchronized (generator) {

			if (square) {
				generator.setShape(SymbolShapeHint.FORCE_SQUARE);
			} else {
				generator.setShape(SymbolShapeHint.FORCE_NONE);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			generator.doQuietZone(true);
			generator.setQuietZone(qz);
			generator.setModuleWidth(scale);

			generator.generateBarcode(canvasProvider, data);
			canvasProvider.finish();
			out.close();

			return out.toByteArray();
		}
	}
}
