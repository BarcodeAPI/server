package org.barcodeapi.server.gen;

import org.barcodeapi.core.utils.StringUtils;
import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;

public class BarcodeGenerator {

	private static final CodeGenerators generators = CodeGenerators.getInstance();

	private BarcodeGenerator() {
	}

	public static CachedBarcode requestBarcode(BarcodeRequest request) throws GenerationException {

		// check for valid render data
		String data = request.getData();
		if (data == null || data.equals("")) {

			throw new GenerationException(ExceptionType.EMPTY);
		}

		// image object
		CachedBarcode barcode = null;

		// is cache allowed
		if (request.useCache()) {

			// lookup image from cache, serve if found
			barcode = BarcodeCache.getBarcode(request.getType(), data);
			if (barcode != null) {
				return barcode;
			}
		}

		// render new image and create its cached object
		CodeGenerator generator = generators.getGenerator(request.getType());
		barcode = new CachedBarcode(generator.getCode(data, request.getOptions()));
		barcode.getProperties().setProperty("type", request.getType().toString());
		barcode.getProperties().setProperty("data", data);
		barcode.getProperties().setProperty("nice", StringUtils.stripIllegal(data));
		barcode.getProperties().setProperty("encd", StringUtils.encode(data));

		// add to cache if allowed
		if (request.useCache()) {

			BarcodeCache.addBarcode(request.getType(), data, barcode);
		}

		return barcode;
	}
}
