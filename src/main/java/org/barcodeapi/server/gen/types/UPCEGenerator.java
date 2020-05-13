package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class UPCEGenerator extends CodeGenerator {

	private UPCEBean generator;

	/**
	 * 
	 */
	public UPCEGenerator() {
		super(CodeType.UPC_E);

		// Setup Code39 generator
		generator = new UPCEBean();

		generator.doQuietZone(true);
		generator.setHeight(UnitConv.in2mm(1));
		generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
	}

	@Override
	public synchronized byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
				out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		double moduleWidth = UnitConv.in2mm(2.5f / dpi);

		// set options and generate
		generator.setModuleWidth(moduleWidth);
		generator.setQuietZone(10 * moduleWidth);
		generator.setVerticalQuietZone(2 * moduleWidth);
		generator.generateBarcode(canvasProvider, data);

		canvasProvider.getBufferedImage();
		canvasProvider.finish();
		out.close();

		return out.toByteArray();
	}
}
