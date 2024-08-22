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
		this.image = BitmapBuilder.prepareImage(dim, getOrientation(), dpi, format);
		this.g2d = BitmapBuilder.prepareGraphics2D(this.image, dim, 0, false);
		this.delegate = new Java2DCanvasProvider(g2d, 0);
		this.delegate.establishDimensions(dim);
		this.g2d.setColor(colorBG);
		this.g2d.fill(new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));
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
