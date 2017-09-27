package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.CodeGenerator;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code128Generator extends CodeGenerator {

	private Code128Bean barcode128Bean;

	private final int dpi = 150;

	public Code128Generator() {
		super(CodeType.Code128);

		barcode128Bean = new Code128Bean();
		barcode128Bean.setCodeset(Code128Constants.CODESET_B);

		// configure barcode generator
		barcode128Bean.setModuleWidth(UnitConv.in2mm(2.5f / dpi));
		barcode128Bean.doQuietZone(true);
		barcode128Bean.setQuietZone(4);
	}

	@Override
	public boolean onRender(String data, File outputFile) {

		try {

			// Open output file
			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode128Bean.generateBarcode(canvasProvider, data);

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
