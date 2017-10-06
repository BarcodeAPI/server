package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code128Generator extends CodeGenerator {

	private Code128Bean barcode128Bean;

	private final int dpi = 150;

	/**
	 * 
	 */
	public Code128Generator() {
		super(CodeType.Code128);

		// Setup Code128 generator
		barcode128Bean = new Code128Bean();

		/**
		 * Character map
		 * 
		 * https://en.wikipedia.org/wiki/Code_128#Bar_code_widths
		 */
		barcode128Bean.setCodeset(Code128Constants.CODESET_B);

		double moduleWidth = UnitConv.in2mm(2.5f / dpi);
		barcode128Bean.setModuleWidth(moduleWidth);

		/**
		 * The minimum width of the Quiet Zone to the left and right of the 128 Bar Code
		 * is 10x, where x is the minimum width of a module. It is mandatory at the left
		 * and right side of the barcode.
		 * 
		 * https://en.wikipedia.org/wiki/Code_128#Quiet_zone
		 * 
		 */
		barcode128Bean.doQuietZone(true);
		barcode128Bean.setQuietZone(moduleWidth);
	}

	@Override
	public void onValidateRequest(String data) {

		if (!data.matches("[ !\"#$%&'()*+,-./0-9:;\\<=\\>?@A-Z[\\]^_`a-z{|}~]{1,24}")) {

			throw new IllegalArgumentException("Invalid Coade128 format.");
		}
	}

	@Override
	public byte[] onRender(String data) {

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode128Bean.generateBarcode(canvasProvider, data);

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
