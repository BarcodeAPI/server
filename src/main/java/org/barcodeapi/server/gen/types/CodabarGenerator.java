package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class CodabarGenerator extends CodeGenerator {

	private CodabarBean generator;

	private final int dpi = 150;

	/**
	 * Constructor for the Codabar generator.
	 */
	public CodabarGenerator() {
		super(CodeType.CODABAR);

		// Setup Codabar generator
		generator = new CodabarBean();

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
		// generator.setVerticalQuietZone(2 * moduleWidth);

		generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);

		generator.setHeight(UnitConv.in2mm(1));
		// barcode128Bean.setBarHeight(UnitConv.in2mm(.5));

		// barcode128Bean.setFontName(name);
		// barcode128Bean.setFontSize(size);
	}

	/**
	 * Called before looking up an image in cache, or rendering;
	 * 
	 * Validate the requested string's format and length.
	 */
	@Override
	public void onValidateRequest(String data) {

		// Validate format
		if (!CodeType.CODABAR.validateFormat(data)) {

			throw new IllegalArgumentException("Invalid Codabar format.");
		}

		// Allow max of 25 characters
		if (data.length() > 25) {

			throw new IllegalArgumentException("Too many characters.");
		}
	}

	/**
	 * Called when an image was not found in cache and must be rendered;
	 * 
	 * Return a PNG image as bytes.
	 */
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

			e.printStackTrace(System.err);
			throw new IllegalStateException("An error has occured.");
		}
	}
}
