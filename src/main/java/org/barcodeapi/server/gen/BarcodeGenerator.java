package org.barcodeapi.server.gen;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.AppConfig;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.json.JSONArray;

import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPooler;
import com.mclarkdev.tools.libobjectpooler.LibObjectPoolerException;

public class BarcodeGenerator {

	private static final CodeGenerators generators = CodeGenerators.getInstance();

	private BarcodeGenerator() {
	}

	public static CachedBarcode requestBarcode(BarcodeRequest request) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		// check for valid render data
		String data = request.getData();
		if (data == null || data.equals("")) {

			throw new GenerationException(ExceptionType.EMPTY);
		}

		// match against blacklist
		JSONArray blacklist = AppConfig.get().getJSONArray("blacklist");
		for (int x = 0; x < blacklist.length(); x++) {

			if (data.matches(blacklist.getString(x))) {

				throw new GenerationException(ExceptionType.BLACKLIST);
			}
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

		// get the generator pool
		LibObjectPooler<CodeGenerator> pool = //
				generators.getGeneratorPool(request.getType());

		CodeGenerator generator = null;

		try {

			// get a generator from the pool
			generator = pool.getWait();

			// render new image and get the bytes
			byte[] png = generator.getCode(data, request.getOptions());

			// create the object to be cached
			barcode = new CachedBarcode(request.getType(), data, png);

		} catch (LibObjectPoolerException e) {

			// failed if unable to get object from pool
			throw new GenerationException(ExceptionType.BUSY);

		} catch (Exception | Error e) {

			// generation itself failed
			throw new GenerationException(ExceptionType.FAILED);
		} finally {

			if (generator != null) {

				// release the generator back to the pool
				pool.release(generator);
			}
		}

		// add to cache if allowed
		if (request.useCache() && barcode != null) {

			BarcodeCache.addBarcode(request.getType(), data, barcode);
		}

		return barcode;
	}
}
