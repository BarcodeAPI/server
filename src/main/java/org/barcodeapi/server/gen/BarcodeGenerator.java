package org.barcodeapi.server.gen;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.TypeSelector;
import org.barcodeapi.server.statistics.StatsCollector;

public class BarcodeGenerator {

	private BarcodeGenerator() {
	}

	public static CachedObject requestBarcode(String target) {

		StatsCollector.getInstance()//
				.incrementCounter("render.total.request");

		// get the request string
		String data = target.substring(1, target.length());

		try {

			// decode the data string
			data = URLDecoder.decode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			throw new IllegalArgumentException("Failed to decode target");
		}

		// use cache if within threshold
		boolean useCache = data.length() <= 64;

		CodeGenerators generators = CodeGenerators.getInstance();

		// process selected type
		CodeGenerator generator;
		CodeType type;

		// parse code type / data string
		int typeIndex = data.indexOf("/");
		if (typeIndex > 0) {

			// get the type string
			String typeString = target.substring(1, typeIndex + 1);

			// type is auto
			if (typeString.equals("auto")) {

				// no type specified
				data = data.substring(5);
				type = TypeSelector.getType(data);
				generator = generators.getGenerator(type);
			} else {

				// check if generator found for given type
				generator = generators.getGenerator(typeString);
				if (generator == null) {

					// no type specified
					type = TypeSelector.getType(data);
					generator = generators.getGenerator(type);
				} else {

					// get generator type and data string
					type = generator.getType();
					data = data.substring(typeIndex + 1);
				}
			}
		} else {

			// no type specified
			type = TypeSelector.getType(data);
			generator = generators.getGenerator(type);
		}

		// check for valid render data
		if (data == null || data.equals("")) {

			throw new IllegalArgumentException("Empty Request");
		}

		// image object
		CachedObject barcode = null;

		// is cache allowed
		if (useCache) {

			// lookup image from cache
			barcode = BarcodeCache.getInstance()//
					.getBarcode(type, data);
		}

		if (barcode == null) {

			byte[] bytes = requestRender(generator, data);

			if (bytes == null) {

				throw new IllegalArgumentException("Bad Request.");
			}

			// create new object with image
			barcode = new CachedObject(bytes);

			// add to cache if allowed
			if (useCache) {

				BarcodeCache.getInstance().addImage(type, data, barcode);
			}
		}

		barcode.getProperties().setProperty("data", data);
		barcode.getProperties().setProperty("nice", stripIllegal(data));
		barcode.getProperties().setProperty("type", type.toString());

		return barcode;
	}

	private static byte[] requestRender(CodeGenerator generator, String data) {

		StatsCollector.getInstance()//
				.incrementCounter("render.total.count");

		// time and render image
		double start = System.nanoTime();
		byte[] image = generator.getCode(data);
		double renderTime = (System.nanoTime() - start) / 1000 / 1000;
		String length = String.format("%.2f", renderTime);

		// add to total render time
		StatsCollector.getInstance()//
				.incrementCounter("render.total.time", renderTime);

		System.out.println(System.nanoTime() + " : " + //
				"Rendered [ " + generator.getType().toString() + " ] " + //
				"with [ " + data + " ] " + //
				"in [ " + length + "ms ] " + //
				"size [ " + image.length + "B ]");

		return image;
	}

	private static String stripIllegal(String data) {

		return data.replaceAll("[!@#$%^&*\\(\\)\\[\\]\\{\\};:\\',\\<\\>\\\"]", "");
	}
}
