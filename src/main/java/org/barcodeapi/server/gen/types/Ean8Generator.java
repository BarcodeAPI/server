package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.statistics.StatsCollector;
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
	public byte[] generateCode(String data) {

		try {

			StatsCollector.getInstance().incrementCounter("ean8.render");

			String fileName = data.replace(File.separatorChar, '-');
			fileName = "ean8" + File.separator + fileName;

			// Open output file
			File outputFile = new File("cache" + File.separator + fileName + ".png");
			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			ean8Bean.generateBarcode(canvasProvider, data);

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
