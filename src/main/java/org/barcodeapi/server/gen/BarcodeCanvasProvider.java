package org.barcodeapi.server.gen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.output.bitmap.BitmapBuilder;
import org.krysalis.barcode4j.output.bitmap.BitmapEncoderRegistry;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

public class BarcodeCanvasProvider extends AbstractCanvasProvider {

	private static final String _MIME = "image/x-png";

	private OutputStream out;
	private int dpi;
	private BufferedImage image;
	private Graphics2D g2d;
	private Java2DCanvasProvider delegate;

	private Color colorBG = Color.white;
	private Color colorFG = Color.black;

	public BarcodeCanvasProvider(OutputStream out, int dpi) {
		super(0);
		this.out = out;
		this.dpi = dpi;
	}

	public void setColors(Color bg, Color fg) {
		this.colorBG = bg;
		this.colorFG = fg;
	}

	public void finish() throws IOException {
		this.image.flush();
		if (this.out != null) {
			BitmapEncoderRegistry.getInstance(_MIME)//
					.encode(this.image, out, _MIME, dpi);
		}
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
