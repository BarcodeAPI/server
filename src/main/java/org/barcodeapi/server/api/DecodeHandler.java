package org.barcodeapi.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
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
import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * DecodeHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class DecodeHandler extends RestHandler {

	// Upload size in bytes
	private static final int UPLOAD_BYTES_MIN = 64;
	private static final int UPLOAD_BYTES_MAX = (4 * 1024 * 1024);

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

	private boolean writeFile(String uid, BufferedImage image) {
		LibMetrics.hitMethodRunCounter();

		try {

			// Write the file to disk
			File outputfile = new File(uploadDir, (uid + ".png"));
			ImageIO.write(image, "png", outputfile);
			return true;
		} catch (IOException e) {

			// Log the write failure
			LibLog._log("Failed to write file to disk.", e);
			return false;
		}
	};

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Assign the request an ID
		String uid = UUID.randomUUID().toString();

		try {

			// Setup accept multi-part
			c.getRequest().setAttribute(Request.MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement("./"));
			if (!c.getRequest().getContentType().startsWith("multipart/")) {

				// Send error to client
				throw new GenerationException(ExceptionType.INVALID, //
						new IllegalArgumentException("Request was not multipart."));
			}

			// Check for image
			if (!c.hasBody() || //
					c.getBodySize() < UPLOAD_BYTES_MIN || //
					c.getBodySize() > UPLOAD_BYTES_MAX) {

				// Send error to client
				throw new GenerationException(ExceptionType.INVALID, //
						new IllegalArgumentException("Body outside accepted size."));
			}

			// Get the uploaded file
			Part part = c.getRequest().getPart("image");

			// Read the image file from user stream
			BufferedImage image = ImageIO.read(part.getInputStream());

			// Write file to disk
			writeFile(uid, image);

			// Check image loaded
			if (image == null) {

				// Send error to client
				LibLog._logF("Failed to parse image for decoding. (%s)", uid);
				throw new GenerationException(ExceptionType.FAILED, //
						new IllegalArgumentException("Failed to parse image."));
			}

			// Check the dimensions for processing
			int width = image.getWidth(), height = image.getHeight();
			if (width < IMAGE_WIDTH_MIN || width > IMAGE_WIDTH_MAX || //
					height < IMAGE_HEIGHT_MIN || height > IMAGE_HEIGHT_MAX) {

				// Send error to client
				throw new GenerationException(ExceptionType.INVALID, //
						new IllegalArgumentException("Invalid image size."));
			}

			// Create a binary bitmap source
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// Decode the barcode
			Result result = new MultiFormatReader().decode(bitmap);

			// Extract the results
			String text = result.getText();
			String format = result.getBarcodeFormat().toString();

			// Log the decode
			long decodeTime = (System.currentTimeMillis() - c.getTimestamp());
			LibLog._logF("Decode complete in %dms. (%s :: %s)", decodeTime, format, text);

			// Send response to client
			r.setStatus(HttpServletResponse.SC_OK);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 200)//
					.put("request", uid)//
					.put("text", text)//
					.put("format", format)//
			).toString());

		} catch (GenerationException e) {

			// Send error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("request", uid)//
					.put("message", e.getMessage())//
			).toString());

		} catch (NotFoundException e) {

			// Send error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 400)//
					.put("request", uid)//
					.put("message", "No barcode found.")//
			).toString());

		} catch (ServletException e) {

			// Send error to client
			LibLog._log("Failed to process upload data.", e);
			r.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			r.setContentType("application/json");
			r.getOutputStream().println((new JSONObject() //
					.put("code", 500)//
					.put("request", uid)//
					.put("message", "Failed to get upload data.")//
					.put("error", e.getMessage())//
			).toString());
		}
	}
}
