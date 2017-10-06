package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Ean8Generator extends CodeGenerator {

	private EAN8Bean ean8Bean;

	private final int dpi = 150;

	public Ean8Generator() {
		super(CodeType.EAN8);

		ean8Bean = new EAN8Bean();

		// configure barcode generator
		ean8Bean.setModuleWidth(UnitConv.in2mm(2.5f / dpi));
		ean8Bean.doQuietZone(true);
		ean8Bean.setQuietZone(4);
	}

	@Override
	public void onValidateRequest(String data) {

		if (!CodeType.EAN8.validateFormat(data)) {

			throw new IllegalArgumentException("Invalid format.");
		}

		if (data.length() == 7) {

			return;
		}

		int checksum = calculateChecksum(data);
		String provided = data.substring(data.length() - 1);
		if (!Integer.toString(checksum).equals(provided)) {

			throw new IllegalArgumentException("Invalid checksum.");
		}
	}

	@Override
	public void onRender(String data, File outputFile) {

		try {

			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			ean8Bean.generateBarcode(canvasProvider, data);

			canvasProvider.getBufferedImage();

			canvasProvider.finish();

			out.close();

		} catch (Exception e) {

			e.printStackTrace();
			throw new IllegalStateException("An error has occured.");
		}
	}

	private int calculateChecksum(String data) {

		if (data.length() < 7 || data.length() > 8) {

			throw new IllegalArgumentException("Invalid length.");
		}

		int sum1 = 0;
		int sum2 = 0;
		for (int x = 0; x < 7; x++) {

			int digit = Character.getNumericValue(data.charAt(x));

			if (x % 2 == 0) {

				sum2 += digit;
			} else {

				sum1 += digit;
			}
		}

		int sum = sum1 + (sum2 * 3);

		int check = (10 - (sum % 10));

		if (check == 10) {

			return 0;
		} else {

			return check;
		}
	}
}
