package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean dataMatrixBean;

	private final int dpi = 200;

	/**
	 * https://en.wikipedia.org/wiki/Data_Matrix
	 */
	public DataMatrixGenerator() {
		super(CodeType.DataMatrix);

		dataMatrixBean = new DataMatrixBean();

		// configure barcode generator
		dataMatrixBean.setQuietZone(2);
		dataMatrixBean.doQuietZone(true);
		dataMatrixBean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
	}

	@Override
	public void onValidateRequest(String data) {

	}

	@Override
	public byte[] onRender(String data) {

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			dataMatrixBean.generateBarcode(canvasProvider, data);

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
