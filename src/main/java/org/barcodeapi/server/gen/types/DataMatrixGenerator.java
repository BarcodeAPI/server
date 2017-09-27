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
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class DataMatrixGenerator extends CodeGenerator {

	private DataMatrixBean dataMatrixBean;

	private final int dpi = 200;

	public DataMatrixGenerator() {
		super(CodeType.DataMatrix);

		dataMatrixBean = new DataMatrixBean();

		// configure barcode generator
		dataMatrixBean.setQuietZone(2);
		dataMatrixBean.doQuietZone(true);
		dataMatrixBean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
	}

	@Override
	public boolean onRender(String data, File outputFile) {

		try {

			// Open output file
			OutputStream out = new FileOutputStream(outputFile);

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			dataMatrixBean.generateBarcode(canvasProvider, data);

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
