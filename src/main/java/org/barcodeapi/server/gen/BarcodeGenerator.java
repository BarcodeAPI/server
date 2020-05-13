package org.barcodeapi.server.gen;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.Blacklist;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONObject;

public class BarcodeGenerator {

	private static final CodeGenerators generators = CodeGenerators.getInstance();

	private BarcodeGenerator() {
	}

	public static CachedBarcode requestBarcode(String target) throws GenerationException {

		// remove [ /api ]
		if (target.startsWith("/api")) {
			target = target.substring(4);
		}

		// get and decode the request string
		String[] parts = target.split("\\?");
		String data = decode(parts[0].substring(1));

		// get and parse options
		JSONObject options = new JSONObject();
		if (parts.length == 2) {
			options = parseOptions(parts[1]);
		}

		// use cache based on options
		boolean useCache = true;
		if (options.length() > 0) {
			useCache = false;
		}
		if (data.length() > 64) {
			useCache = false;
		}
		if (options.optBoolean("no-cache", false)) {
			useCache = false;
		}

		// process selected type
		CodeGenerator generator;
		CodeType type;

		// parse code type / data string
		int typeIndex = data.indexOf("/");
		if (typeIndex > 0) {

			// get the type string
			String typeString = data.substring(0, typeIndex);

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

			throw new GenerationException(ExceptionType.EMPTY);
		}

		// match against blacklist
		for (String invalid : Blacklist.getBlacklist()) {

			if (data.matches(invalid)) {

				throw new GenerationException(ExceptionType.BLACKLIST);
			}
		}

		// image object
		CachedBarcode barcode = null;

		// is cache allowed
		if (useCache) {

			// lookup image from cache, serve if found
			barcode = BarcodeCache.getBarcode(type, data);
			if (barcode != null) {
				return barcode;
			}
		}

		// render new image and create its cached object
		barcode = new CachedBarcode(generator.getCode(data, options));
		barcode.getProperties().setProperty("type", type.toString());
		barcode.getProperties().setProperty("data", data);
		barcode.getProperties().setProperty("nice", stripIllegal(data));
		barcode.getProperties().setProperty("encd", encode(data));

		// add to cache if allowed
		if (useCache) {

			BarcodeCache.addBarcode(type, data, barcode);
		}

		return barcode;
	}

	private static JSONObject parseOptions(String opts) {

		JSONObject options = new JSONObject();

		String[] parts = opts.split("&");

		for (String option : parts) {

			String[] kv = option.split("=");
			options.put(kv[0], kv[1]);
		}

		return options;
	}

	public static String decode(String data) throws GenerationException {

		try {

			return URLDecoder.decode(data, "UTF-8");

		} catch (UnsupportedEncodingException | IllegalArgumentException e) {

			throw new GenerationException(ExceptionType.INVALID, e);
		}
	}

	private static String encode(String data) {

		try {

			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception e) {

			return null;
		}
	}

	private static String stripIllegal(String data) {

		return data.replaceAll("[!@#$%^&*\\(\\)\\[\\]\\{\\};:\\',\\<\\>\\\"]", "");
	}
}
