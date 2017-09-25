package org.barcodeapi.server.code128;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ImageCache;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class BarcodeServer extends AbstractHandler {

	final int dpi = 150;

	Code128Bean barcode128Bean;

	public BarcodeServer() {

		barcode128Bean = new Code128Bean();
		barcode128Bean.setCodeset(Code128Constants.CODESET_B);

		// Configure the barcode generator
		// adjust barcode width here
		barcode128Bean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
		barcode128Bean.doQuietZone(true);

		ImageCache.getInstance().createCache(CodeType.Code128);
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String data = target.substring(1, target.length());

		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Server", "barcodeapi.org");
		baseRequest.setHandled(true);

		// load from cache
		byte[] image = ImageCache.getInstance().getImage(CodeType.Code128, data);

		if (image != null) {

			System.out.println("Served from cache [ " + data + " ]");
		} else {

			// render image
			long start = System.currentTimeMillis();
			image = render(data);
			long time = System.currentTimeMillis() - start;

			System.out.println("Rendered [ " + data + " ] in [ " + time + "ms ]");

			ImageCache.getInstance().addImage(CodeType.Code128, data, image);
		}

		// print to stream
		response.setHeader("Content-Type", "image/jpg");
		response.setHeader("Content-Length", Integer.toString(image.length));
		response.getOutputStream().write(image);
	}

	public byte[] render(String data) throws IOException {

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
	}
}