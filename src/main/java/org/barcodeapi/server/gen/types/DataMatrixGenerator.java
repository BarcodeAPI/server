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
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean dataMatrixBean;

	private final int dpi = 150;

	public DataMatrixGenerator() {

		dataMatrixBean = new DataMatrixBean();

		// configure barcode generator
		dataMatrixBean.setQuietZone(5);
		dataMatrixBean.doQuietZone(true);
		dataMatrixBean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));

	}

	@Override
	public byte[] generateCode(String data) {

		try {

			StatsCollector.getInstance().incrementCounter("matrix.render");

			String fileName = data.replace(File.separatorChar, '-');
			fileName = "matrix" + File.separator + fileName;

			// Open output file
			File outputFile = new File("cache" + File.separator + fileName + ".png");
			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			dataMatrixBean.generateBarcode(canvasProvider, data);

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
