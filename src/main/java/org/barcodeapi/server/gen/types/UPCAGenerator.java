package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class UPCAGenerator extends CodeGenerator {

	private UPCABean generator;

	private final int dpi = 150;

	/**
	 * 
	 */
	public UPCAGenerator() {
		super(CodeType.UPC_A);

		// Setup Code39 generator
		generator = new UPCABean();

		double moduleWidth = UnitConv.in2mm(2.5f / dpi);
		generator.setModuleWidth(moduleWidth);

		/**
		 * Set quiet zone
		 */
		generator.doQuietZone(true);
		generator.setQuietZone(10 * moduleWidth);
		generator.setVerticalQuietZone(2 * moduleWidth);

		generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);

		generator.setHeight(UnitConv.in2mm(1));
	}

	@Override
	public byte[] onRender(String data) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
				out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		generator.generateBarcode(canvasProvider, data);

		canvasProvider.getBufferedImage();
		canvasProvider.finish();
		out.close();

		return out.toByteArray();
	}
}
