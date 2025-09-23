package org.barcodeapi.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * DecodeHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class DecodeHandler extends RestHandler {

	// Upload size in bytes
	private static final int UPLOAD_BYTES_MIN = 64;
	private static final int UPLOAD_BYTES_MAX = (2 * 1024 * 1024);

	// Upload size dimensions
	private static final int IMAGE_WIDTH_MIN = 32;
	private static final int IMAGE_WIDTH_MAX = 2400;
	private static final int IMAGE_HEIGHT_MIN = 32;
	private static final int IMAGE_HEIGHT_MAX = 2400;

	private final File uploadDir;

	public DecodeHandler() {
		super(
				// Authentication not required
				false,
				// Use client rate limit
				true,
				// Do not create new sessions
				false);

		this.uploadDir = new File("uploads", "decode");
		this.uploadDir.mkdirs();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Setup accept multi-part
		c.getRequest().setAttribute(Request.MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement("./"));
		if (!c.getRequest().getContentType().startsWith("multipart/")) {
			// TODO not multi-part
			return;
		}

		// Check for image
		if (!c.hasBody() || //
				c.getBodySize() < UPLOAD_BYTES_MIN || //
				c.getBodySize() > UPLOAD_BYTES_MAX) {
			// TODO invalid upload size
			return;
		}

		String uid = UUID.randomUUID().toString();

		try {

			// Get the uploaded file
			Part part = c.getRequest().getPart("image");

			// Read the image file
			BufferedImage image = ImageIO.read(part.getInputStream());

			if (image == null) {
				// TODO unable to read as image
				return;
			}

			// The output file path
			File outputfile = new File(uploadDir, (uid + ".png"));

			// Save image file to disk
			ImageIO.write(image, "png", outputfile);

			// Check the dimensions for processing
			int width = image.getWidth(), height = image.getHeight();
			if (width < IMAGE_WIDTH_MIN || width > IMAGE_WIDTH_MAX || //
					height < IMAGE_HEIGHT_MIN || height > IMAGE_HEIGHT_MAX) {
				// TODO invalid image size
				return;
			}

			// Create a binary bitmap source
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// Decode the barcode
			Result result = new MultiFormatReader().decode(bitmap);

			// Extract the results
			String text = result.getText();
			String format = result.getBarcodeFormat().toString();

			// Send response to client
			r.setStatus(HttpServletResponse.SC_OK);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 200)//
					.put("text", text)//
					.put("format", format)//
			).toString());

		} catch (NotFoundException e) {

			// Send error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("message", "no barcode found")//
			).toString());
		} catch (Exception e) {

			// Send error to client
			r.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			r.setHeader("Content-Type", "application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 500)//
					.put("text", "decode failed")//
			).toString());
		}
	}
}
