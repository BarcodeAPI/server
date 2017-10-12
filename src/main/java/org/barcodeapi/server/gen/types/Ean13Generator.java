package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Ean13Generator extends CodeGenerator {

	private EAN13Bean generator;

	private final int dpi = 150;

	public Ean13Generator() {
		super(CodeType.EAN13);

		generator = new EAN13Bean();

		// configure barcode generator
		generator.setModuleWidth(UnitConv.in2mm(2.5f / dpi));
		generator.doQuietZone(true);
		generator.setQuietZone(4);
	}

	@Override
	public void onValidateRequest(String data) {

		if (data.length() == 12) {

			return;
		}

		int checksum = CodeUtils.calculateEanChecksum(data);
		String provided = data.substring(data.length() - 1);
		if (!Integer.toString(checksum).equals(provided)) {

			throw new IllegalArgumentException("Invalid checksum");
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
