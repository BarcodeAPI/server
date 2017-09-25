package org.barcodeapi.server.code128;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class BarcodeServer extends AbstractHandler {

	final int dpi = 100;

	Code128Bean barcode128Bean;

	public BarcodeServer() {

		barcode128Bean = new Code128Bean();
		barcode128Bean.setCodeset(Code128Constants.CODESET_B);

		// Configure the barcode generator
		// adjust barcode width here
		barcode128Bean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
		barcode128Bean.doQuietZone(false);
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String data = target.substring(1, target.length());

		response.addHeader("Server", "barcodeapi.org");

		long start = System.currentTimeMillis();
		render(data);
		long time = System.currentTimeMillis() - start;

		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getOutputStream().println("Rendered [ " + data + " ] in [ " + time + "ms ]");
	}

	public void render(String data) throws IOException {

		StatsCollector.getInstance().incrementCounter("code128.render");

		System.out.println("Rendering: " + data);

		String fileName = data.replace(File.separatorChar, '-');
		fileName = "128" + File.separator + fileName;

		// Open output file
		File outputFile = new File("cache" + File.separator + fileName + ".png");
		OutputStream out = new FileOutputStream(outputFile);
		try {

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode128Bean.generateBarcode(canvasProvider, data);

			canvasProvider.finish();
		} finally {

			out.close();
		}
	}
}