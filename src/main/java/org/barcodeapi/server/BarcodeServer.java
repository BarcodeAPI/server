package org.barcodeapi.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.naming.ConfigurationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.xml.sax.SAXException;

public class BarcodeServer extends AbstractHandler {

	public BarcodeServer() throws ConfigurationException, SAXException, IOException, BarcodeException {

	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.addHeader("Server", "barcodeapi.org");

		String[] requestParts = target.split("\\/");

		if (requestParts.length == 0) {

			// TODO display home page
		}

		String data = requestParts[2];

		switch (requestParts[1]) {

		case "128":
		case "matrix":
		case "qr":
			render(data);
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			break;

		default:
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Barcode type unavailalbe.");
			baseRequest.setHandled(false);
			break;
		}

	}

	public void render(String data) throws IOException {

		System.out.print("Rendering: " + data);

		Code128Bean barcode128Bean = new Code128Bean();

		barcode128Bean.setCodeset(Code128Constants.CODESET_B);
		final int dpi = 100;

		// Configure the barcode generator
		// adjust barcode width here
		barcode128Bean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
		barcode128Bean.doQuietZone(false);

		// Open output file
		File outputFile = new File("cache" + File.separator + data + ".png");
		OutputStream out = new FileOutputStream(outputFile);
		try {
			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(out, "image/x-png", dpi,
					BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode128Bean.generateBarcode(canvasProvider, data);

			canvasProvider.finish();
		} finally {
			out.close();
		}
	}
}