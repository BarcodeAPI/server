package org.barcodeapi.server.gen;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.json.JSONArray;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPooler;
import com.mclarkdev.tools.libobjectpooler.LibObjectPoolerException;

/**
 * BarcodeGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeGenerator {

	private static final CodeGenerators generators = CodeGenerators.getInstance();

	private BarcodeGenerator() {
		throw new IllegalStateException();
	}

	public static CachedBarcode requestBarcode(String target) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		return requestBarcode(//
				new BarcodeRequest(target));
	}

	public static CachedBarcode requestBarcode(BarcodeRequest request) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		// start request processing timer
		long timeStart = System.currentTimeMillis();

		// get the type of renderer
		CodeType type = request.getType();
		String name = type.getName();

		// check for valid render data
		String data = request.getData();
		if (data == null || data.equals("")) {

			// fail on empty requests
			throw new GenerationException(ExceptionType.EMPTY);
		}

		// match against blacklist entries
		JSONArray blacklist = AppConfig.get().getJSONArray("blacklist");
		for (int x = 0; x < blacklist.length(); x++) {

			// fail if request matches blacklist entry
			if (data.matches(blacklist.getString(x))) {
				throw new GenerationException(ExceptionType.BLACKLIST);
			}
		}

		// validate barcode pattern
		if (!data.matches(type.getPatternExtended())) {

			// fail if request does not match pattern
			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Invalid data for selected code type."));
		}

		// the barcode image object
		CachedBarcode barcode = null;

		// is cache allowed
		if (request.useCache()) {

			// lookup image from cache
			CachedObject obj = ObjectCache//
					.getCache(name).get(data);

			// return if found
			if (obj != null) {
				return ((CachedBarcode) obj);
			}
		}

		// get the generator pool
		LibObjectPooler<CodeGenerator> pool = //
				generators.getGeneratorPool(name);

		CodeGenerator generator = null;

		try {

			// get a generator from the pool
			generator = pool.getWait();

			// update global and engine counters
			LibMetrics.instance().hitCounter("render", "count");
			LibMetrics.instance().hitCounter("render", "type", name, "count");

			// encode control characters
			String encoded = data;
			if (type.getAllowNonprinting()) {
				encoded = CodeUtils.parseControlChars(data);
			}

			// run implementation specific validations
			generator.onValidateRequest(encoded);

			// render new image and get the bytes
			byte[] png = generator.onRender(request);

			// calculate run time and log generation
			int time = (int) (System.currentTimeMillis() - timeStart);
			LibLog.clogF("barcode", "I0601", name, data, png.length, time);

			// create the object to be cached
			barcode = new CachedBarcode(name, data, png);

			// add to cache if allowed
			if (request.useCache()) {
				ObjectCache.getCache(name).put(data, barcode);
			}

		} catch (GenerationException e) {

			// update global and engine counters
			LibMetrics.instance().hitCounter("render", "invalid");
			LibMetrics.instance().hitCounter("render", "type", name, "invalid");

			// pass it up
			throw e;

		} catch (LibObjectPoolerException e) {

			// Update global and engine counters
			LibMetrics.instance().hitCounter("render", "busy");
			LibMetrics.instance().hitCounter("render", "type", name, "busy");

			// Failed if unable to get object from pool
			throw new GenerationException(ExceptionType.BUSY);

		} catch (Exception | Error e) {

			// Update global and engine counters
			LibMetrics.instance().hitCounter("render", "fail");
			LibMetrics.instance().hitCounter("render", "type", name, "fail");

			// Generation itself failed
			throw new GenerationException(ExceptionType.FAILED);
		} finally {

			// Update global and engine counters
			int time = (int) (System.currentTimeMillis() - timeStart);
			LibMetrics.instance().hitCounter(time, "render", "time");
			LibMetrics.instance().hitCounter(time, "render", "type", name, "time");

			// Release the generator back to the pool
			if (generator != null) {
				pool.release(generator);
			}
		}

		// Return the barcode
		return barcode;
	}
}
