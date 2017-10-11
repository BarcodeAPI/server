package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean generator;

	private final int dpi = 200;

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public DataMatrixGenerator() {
		super(CodeType.DataMatrix);

		generator = new DataMatrixBean();

		// configure barcode generator
		generator.setQuietZone(2);
		generator.doQuietZone(true);
		generator.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
	}

	@Override
	public void onValidateRequest(String data) {

		if (!CodeType.DataMatrix.validateFormat(data)) {

			throw new IllegalArgumentException("Invalid format.");
		}
	}

	@Override
	public byte[] onRender(String data) {

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			generator.generateBarcode(canvasProvider, data);

			canvasProvider.getBufferedImage();

			canvasProvider.finish();

			out.close();

			return out.toByteArray();

		} catch (Exception e) {

			e.printStackTrace();
			throw new IllegalStateException("An error has occured.");
		}
	}
}
