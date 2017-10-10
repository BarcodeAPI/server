package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code39Generator extends CodeGenerator {

	private Code39Bean barcode39Bean;

	private final int dpi = 150;

	/**
	 * 
	 */
	public Code39Generator() {
		super(CodeType.Code39);

		// Setup Code39 generator
		barcode39Bean = new Code39Bean();

		double moduleWidth = UnitConv.in2mm(2.5f / dpi);
		barcode39Bean.setModuleWidth(moduleWidth);

		/**
		 * Set quiet zone
		 */
		barcode39Bean.doQuietZone(true);
		barcode39Bean.setQuietZone(10 * moduleWidth);
		barcode39Bean.setVerticalQuietZone(2 * moduleWidth);

		barcode39Bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);

		barcode39Bean.setHeight(UnitConv.in2mm(1));
		// barcode39Bean.setBarHeight(UnitConv.in2mm(.5));

		// barcode39Bean.setFontName(name);
		// barcode39Bean.setFontSize(size);
	}

	@Override
	public void onValidateRequest(String data) {

		/**
		 * Validate against Code39 specifications.
		 * 
		 * https://en.wikipedia.org/wiki/Code_39#Encoding
		 */
		if (!data.matches("[A-Z0-9* -$%.\\/+]+")) {

			throw new IllegalArgumentException("Invalid Coade128 format.");
		}

		// Allow max of 50 characters
		if (data.length() > 50) {

			throw new IllegalArgumentException("Too many characters.");
		}
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
