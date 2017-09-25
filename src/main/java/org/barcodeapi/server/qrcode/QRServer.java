package org.barcodeapi.server.qrcode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ImageCache;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRServer extends AbstractHandler {

	final int dpi = 150;

	public QRServer() {

		ImageCache.getInstance().createCache(CodeType.QRCode);
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String data = target.substring(1, target.length());

		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Server", "barcodeapi.org");
		baseRequest.setHandled(true);

		// load from cache
		byte[] image = ImageCache.getInstance().getImage(CodeType.QRCode, data);

		if (image != null) {

			System.out.println("Serving from cache [ " + data + " ]");

		} else {

			// render image
			long start = System.currentTimeMillis();
			image = render(data);
			long time = System.currentTimeMillis() - start;

			System.out.println("Rendered [ " + data + " ] in [ " + time + "ms ]");

			ImageCache.getInstance().addImage(CodeType.QRCode, data, image);
		}

		// print to stream
		response.setHeader("Content-Type", "image/jpg");
		response.setHeader("Content-Length", Integer.toString(image.length));
		response.getOutputStream().write(image);
	}

	public byte[] render(String data) throws IOException {

		StatsCollector.getInstance().incrementCounter("qr.render");

		System.out.println("Rendering: " + data);

		String fileName = data.replace(File.separatorChar, '-');
		fileName = "cache" + File.separator + "qr" + File.separator + fileName + ".png";

		// Open output file
		try {

			int mWidth = 300;
			int mHeight = 300;

			Map<EncodeHintType, Object> hintsMap = new HashMap<>();
			hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hintsMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			hintsMap.put(EncodeHintType.MARGIN, 2);

			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);

			Path path = Paths.get(fileName);
			MatrixToImageWriter.writeToPath(bitMatrix, "png", path);

			return Files.readAllBytes(path);

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}
}