package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Ean13Generator extends CodeGenerator {

	private EAN13Bean ean13Bean;

	private final int dpi = 150;

	public Ean13Generator() {
		super(CodeType.EAN13);

		ean13Bean = new EAN13Bean();

		// configure barcode generator
		ean13Bean.setModuleWidth(UnitConv.in2mm(2.5f / dpi));
		ean13Bean.doQuietZone(true);
		ean13Bean.setQuietZone(4);
	}

	@Override
	public boolean onRender(String data, File outputFile) {

		try {

			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			ean13Bean.generateBarcode(canvasProvider, data);

			canvasProvider.getBufferedImage();

			canvasProvider.finish();

			out.close();

			return true;

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}
}
