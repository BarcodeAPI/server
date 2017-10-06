package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code39Generator extends CodeGenerator {

	private Code39Bean barcode39Bean;

	private final int dpi = 150;

	public Code39Generator() {
		super(CodeType.Code39);

		barcode39Bean = new Code39Bean();

		// configure barcode generator
		barcode39Bean.setModuleWidth(UnitConv.in2mm(2.5f / dpi));
		barcode39Bean.doQuietZone(true);
		barcode39Bean.setQuietZone(4);
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

			barcode39Bean.generateBarcode(canvasProvider, data);

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
