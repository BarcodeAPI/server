package org.barcodeapi.server.gen.types;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.impl.DefaultZXingProvider;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mclarkdev.tools.liblog.LibLog;

/**
 * QRCodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class QRDotCodeGenerator extends DefaultZXingProvider {

	public QRDotCodeGenerator(CodeType codeType) {

		// Setup QR generator
		super(codeType, BarcodeFormat.QR_CODE, new QRCodeWriter());
	}

	@Override
	public byte[] onRender(BarcodeRequest request) throws Exception {

		JSONObject options = request.getOptions();

		HashMap<String, Object> defaults = //
				request.getType().getDefaults();

		int size = options.optInt("size", //
				(Integer) defaults.getOrDefault("size", 275));

		int qz = options.optInt("qz", //
				(Integer) defaults.getOrDefault("qz", 4));

		// String correction = options.optString("correction", //
		// (String) defaults.getOrDefault("correction", "M"));

		Map<EncodeHintType, Object> hintsMap = new HashMap<>();
		// hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
		// hintsMap.put(EncodeHintType.ERROR_CORRECTION, //
		// ErrorCorrectionLevel.valueOf(correction));
		hintsMap.put(EncodeHintType.MARGIN, qz);

		BitMatrix bitMatrix;
		synchronized (generator) {

			bitMatrix = generator.encode(//
					request.getData(), format, 1, 1, hintsMap);
		}

		int outputSize = size;

		int modules = bitMatrix.getWidth();

		LibLog._logF("REAL modules=%d", modules);

		double moduleSize = (double) outputSize / modules;
		double diameter = moduleSize * 0.75;

		BufferedImage img = new BufferedImage(//
				outputSize, outputSize, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();

		// Enable antialiasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background is white
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, outputSize, outputSize);

		// Pixels are black
		g.setColor(Color.BLACK);

		for (int y = 0; y < modules; y++) {
			for (int x = 0; x < modules; x++) {

				// Skip white pixels
				if (!bitMatrix.get(x, y)) {
					continue;
				}

				// Calculate the mask location, and add the circle
				double px = (x * moduleSize) + ((moduleSize - diameter) / 2.0);
				double py = (y * moduleSize) + ((moduleSize - diameter) / 2.0);
				g.fill(new Ellipse2D.Double(px, py, diameter, diameter));
			}
		}

		g.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);

		return out.toByteArray();
	}
}
