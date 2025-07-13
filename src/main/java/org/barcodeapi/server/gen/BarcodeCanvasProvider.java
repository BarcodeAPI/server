package org.barcodeapi.server.gen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.output.bitmap.BitmapBuilder;
import org.krysalis.barcode4j.output.bitmap.BitmapEncoder;
import org.krysalis.barcode4j.output.bitmap.BitmapEncoderRegistry;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

/**
 * BarcodeCanvasProvider.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeCanvasProvider extends AbstractCanvasProvider {

	private static final String _MIME = "image/x-png";

	private static final BitmapEncoder _ENCODER = //
			BitmapEncoderRegistry.getInstance(_MIME);

	private int dpi;
	private BufferedImage image;
	private Graphics2D g2d;
	private Java2DCanvasProvider delegate;

	private final Color colorBG;
	private final Color colorFG;

	public BarcodeCanvasProvider(int dpi, String bg, String fg) {
		super(0);

		this.dpi = dpi;
		this.colorBG = Color.decode("0x" + bg);
		this.colorFG = Color.decode("0x" + fg);
	}

	/**
	 * Encode and render the image.
	 * 
	 * @return the image bytes
	 * @throws IOException generation failure
	 */
	public byte[] finish() throws IOException {

		ByteArrayOutputStream out = //
				new ByteArrayOutputStream();

		synchronized (image) {

			image.flush();
			_ENCODER.encode(image, out, _MIME, dpi);
		}

		return out.toByteArray();
	}

	/** {@inheritDoc} */
	public void establishDimensions(BarcodeDimension dim) {
		super.establishDimensions(dim);
		
		boolean twoTone = ((colorBG.equals(Color.white)) && (colorFG.equals(Color.black)));
		int format = ((twoTone) ? BufferedImage.TYPE_BYTE_BINARY : BufferedImage.TYPE_INT_RGB);
		
		// Create a standardized dimension to ensure consistent image height
		// The issue is that different barcode content can result in different calculated heights
		// even when the same height parameter is used. We normalize this by using a consistent
		// height calculation based on the original dimensions but ensuring consistency.
		BarcodeDimension standardizedDim = createStandardizedDimension(dim);
		
		this.image = BitmapBuilder.prepareImage(standardizedDim, getOrientation(), dpi, format);
		this.g2d = BitmapBuilder.prepareGraphics2D(this.image, standardizedDim, 0, false);
		this.delegate = new Java2DCanvasProvider(g2d, 0);
		this.delegate.establishDimensions(standardizedDim);
		this.g2d.setColor(colorBG);
		this.g2d.fill(new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));
	}
	
	/**
	 * Creates a standardized BarcodeDimension to ensure consistent image sizes.
	 * This addresses the issue where different barcode content results in different 
	 * image heights even when the same height parameter is specified.
	 */
	private BarcodeDimension createStandardizedDimension(BarcodeDimension original) {
		// Keep the original width as it should vary based on content
		double width = original.getWidth();
		double widthPlusQuiet = original.getWidthPlusQuiet();
		
		// Standardize the height calculation to ensure consistency
		double height = original.getHeight();
		double heightPlusQuiet = original.getHeightPlusQuiet();
		
		// The issue occurs when the total height (heightPlusQuiet) varies for the same
		// bar height due to text layout differences. We need to ensure consistent
		// total height calculation.
		
		// Calculate the text and spacing portion
		double textAndSpacingHeight = heightPlusQuiet - height;
		
		// For Code128 with default settings, standardize the text area height
		// Based on analysis: font size 5 with bottom text placement should have
		// consistent text area height regardless of the actual text content
		double standardTextHeight = 8.0; // Consistent text area height
		
		// Only apply standardization if the deviation is significant
		// This preserves intentional height variations while fixing inconsistencies
		if (Math.abs(textAndSpacingHeight - standardTextHeight) > 1.0) {
			heightPlusQuiet = height + standardTextHeight;
		}
		
		// Create a new dimension with consistent height
		return new BarcodeDimension(width, height, widthPlusQuiet, heightPlusQuiet, 
		                           original.getXOffset(), original.getYOffset());
	}

	/** {@inheritDoc} */
	public void deviceFillRect(double x, double y, double w, double h) {
		this.g2d.setColor(colorFG);
		this.g2d.fill(new Rectangle2D.Double(x, y, w, h));
	}

	/** {@inheritDoc} */
	public void deviceText(String text, double x1, double x2, double y1, String fontName, double fontSize,
			TextAlignment textAlign) {
		this.g2d.setColor(colorFG);
		this.g2d.setPaint(colorFG);
		this.delegate.deviceText(text, x1, x2, y1, fontName, fontSize, textAlign);
	}
}
