package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code128Generator extends CodeGenerator {

	private Code128Bean generator;

	private final int dpi = 150;

	/**
	 * 
	 */
	public Code128Generator() {
		super(CodeType.Code128);

		// Setup Code128 generator
		generator = new Code128Bean();

		// barcode128Bean.setBarHeight(height);
		double moduleWidth = UnitConv.in2mm(2.5f / dpi);
		generator.setModuleWidth(moduleWidth);

		/**
		 * The minimum width of the Quiet Zone to the left and right of the 128 Bar Code
		 * is 10x, where x is the minimum width of a module. It is mandatory at the left
		 * and right side of the barcode.
		 * 
		 * https://en.wikipedia.org/wiki/Code_128#Quiet_zone
		 * 
		 */
		generator.doQuietZone(true);
		generator.setQuietZone(10 * moduleWidth);
		generator.setVerticalQuietZone(2 * moduleWidth);

		generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);

		generator.setHeight(UnitConv.in2mm(1));
		// barcode128Bean.setBarHeight(UnitConv.in2mm(.5));

		// barcode128Bean.setFontName(name);
		// barcode128Bean.setFontSize(size);
	}

	@Override
	public void onValidateRequest(String data) {
	}

	@Override
	public byte[] onRender(String data) {

		String newData = "";

		for (int x = 0; x < data.length(); x++) {

			if (data.charAt(x) == '$' && data.charAt(x + 1) == '$') {

				newData += (char)(((int) data.charAt(x+=2)) - 64);
			} else {

				newData += data.charAt(x);
			}
		}

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			generator.generateBarcode(canvasProvider, newData);

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
