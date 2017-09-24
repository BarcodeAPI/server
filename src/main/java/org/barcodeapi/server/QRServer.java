package org.barcodeapi.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRServer extends AbstractHandler {

	final int dpi = 100;

	public QRServer() {

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

		System.out.println("Rendering: " + data);

		String fileName = data.replace(File.separatorChar, '-');
		fileName = "cache" + File.separator + "qr" + File.separator + fileName + ".png";

		// Open output file
		try {

			int mWidth = 400;
			int mHeight = 400;

			Map<EncodeHintType, Object> hintsMap = new HashMap<>();
			hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hintsMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			hintsMap.put(EncodeHintType.MARGIN, 5);

			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);

			MatrixToImageWriter.writeToPath(bitMatrix, "png", Paths.get(fileName));

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}