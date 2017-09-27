package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.statistics.StatsCollector;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code128Generator extends CodeGenerator {

	Code128Bean barcode128Bean;

	final int dpi = 150;

	public Code128Generator() {

		barcode128Bean = new Code128Bean();
		barcode128Bean.setCodeset(Code128Constants.CODESET_B);

		// Configure the barcode generator
		// adjust barcode width here
		barcode128Bean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
		barcode128Bean.doQuietZone(true);
		barcode128Bean.setQuietZone(10);
	}

	@Override
	public byte[] generateCode(String data) {

		try {

			StatsCollector.getInstance().incrementCounter("code128.render");

			System.out.println("Rendering: " + data);

			String fileName = data.replace(File.separatorChar, '-');
			fileName = "128" + File.separator + fileName;

			// Open output file
			File outputFile = new File("cache" + File.separator + fileName + ".png");
			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode128Bean.generateBarcode(canvasProvider, data);

			canvasProvider.getBufferedImage();

			canvasProvider.finish();

			out.close();

			Path path = Paths.get(outputFile.getAbsolutePath());

			return Files.readAllBytes(path);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}
}
