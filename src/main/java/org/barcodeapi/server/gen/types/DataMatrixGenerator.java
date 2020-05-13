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

		// configure barcode generator
		generator.doQuietZone(true);
		generator.setShape(SymbolShapeHint.FORCE_SQUARE);
	}

	@Override
	public String onValidateRequest(String data) {

		return CodeUtils.parseControlChars(data);
	}

	@Override
	public synchronized byte[] onRender(String data, JSONObject options) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int dpi = options.optInt("dpi", 200);
		int quiet = options.optInt("qz", (dpi / 10));
		double size = options.optDouble("size", 1.5);

		BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
				out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		generator.setQuietZone(quiet);
		generator.setModuleWidth(size);
		generator.generateBarcode(canvasProvider, data);

		canvasProvider.getBufferedImage();

		canvasProvider.finish();

		out.close();

		return out.toByteArray();
	}
}
