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
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class Code39Generator extends CodeGenerator {

	private Code39Bean barcode39Bean;

	private final int dpi = 150;

	public Code39Generator() {

		barcode39Bean = new Code39Bean();

		// configure barcode generator
		barcode39Bean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
		barcode39Bean.doQuietZone(true);
		barcode39Bean.setQuietZone(4);
	}

	@Override
	public byte[] generateCode(String data) {

		try {

			StatsCollector.getInstance().incrementCounter("code128.render");

			String fileName = data.replace(File.separatorChar, '-');
			fileName = "39" + File.separator + fileName;

			// Open output file
			File outputFile = new File("cache" + File.separator + fileName + ".png");
			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode39Bean.generateBarcode(canvasProvider, data);

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
