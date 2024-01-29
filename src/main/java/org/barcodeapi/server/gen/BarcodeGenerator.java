package org.barcodeapi.server.gen;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.json.JSONArray;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPooler;
import com.mclarkdev.tools.libobjectpooler.LibObjectPoolerException;

public class BarcodeGenerator {

	private static final CodeGenerators generators = CodeGenerators.getInstance();

	private BarcodeGenerator() {
	}

	public static CachedBarcode requestBarcode(BarcodeRequest request) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		// name of renderer
		String name = request.getType().getName();

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

		// validate code format
		if (!data.matches(request.getType().getPatternExtended())) {

			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Invalid data for selected code type"));
		}

		// image object
		CachedBarcode barcode = null;

		// is cache allowed
		if (request.useCache()) {

			// lookup image from cache, serve if found
			barcode = BarcodeCache.getBarcode(request);
			if (barcode != null) {
				return barcode;
			}
		}

		// get the generator pool
		LibObjectPooler<CodeGenerator> pool = //
				generators.getGeneratorPool(request.getType().getName());

		CodeGenerator generator = null;

		// start timer and render
		long timeStart = System.currentTimeMillis();

		try {

			// get a generator from the pool
			generator = pool.getWait();

			// update global and engine counters
			LibMetrics.instance().hitCounter("render", "count");
			LibMetrics.instance().hitCounter("render", "type", name, "count");

			// any additional generator validations
			String validated = generator.onValidateRequest(data);

			// render new image and get the bytes
			byte[] png = generator.onRender(validated, request.getOptions());

			int time = (int) (System.currentTimeMillis() - timeStart);

			// log the render
			LibLog.clogF("barcode", "I0601", name, data, png.length, time);

			// create the object to be cached
			barcode = new CachedBarcode(request.getType(), data, png);

		} catch (LibObjectPoolerException e) {

			// update global and engine counters
			LibMetrics.instance().hitCounter("render", "fail");
			LibMetrics.instance().hitCounter("render", "type", name, "fail");

			// failed if unable to get object from pool
			throw new GenerationException(ExceptionType.BUSY);

		} catch (Exception | Error e) {

			// update global and engine counters
			LibMetrics.instance().hitCounter("render", "fail");
			LibMetrics.instance().hitCounter("render", "type", name, "fail");

			// generation itself failed
			throw new GenerationException(ExceptionType.FAILED);
		} finally {

			// update global and engine counters
			int time = (int) (System.currentTimeMillis() - timeStart);
			LibMetrics.instance().hitCounter(time, "render", "time");
			LibMetrics.instance().hitCounter(time, "render", "type", name, "time");

			if (generator != null) {

				// release the generator back to the pool
				pool.release(generator);
			}
		}

		// add to cache if allowed
		if (request.useCache() && barcode != null) {

			BarcodeCache.addBarcode(request.getType().getName(), data, barcode);
		}

		return barcode;
	}
}
