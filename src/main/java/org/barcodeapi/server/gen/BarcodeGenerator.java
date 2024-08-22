package org.barcodeapi.server.gen;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;

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

	public static CachedBarcode requestBarcode(String uri) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		return requestBarcode(BarcodeRequest.fromURI(uri));
	}

	public static CachedBarcode requestBarcode(BarcodeRequest request) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		// Start request processing timer
		long start = System.currentTimeMillis();

		// Get the render request data
		String type = request.getType().getName();
		String data = request.getData();

		// The barcode image object
		CachedBarcode barcode = null;

		// Is cache allowed
		if (request.useCache()) {

			// Lookup image from cache
			CachedObject obj = ObjectCache//
					.getCache(type).get(data);

			// Return if found
			if (obj != null) {
				return ((CachedBarcode) obj);
			}
		}

		// Get the generator pool for the type
		LibObjectPooler<CodeGenerator> pool = //
				generators.getGeneratorPool(type);

		CodeGenerator generator = null;

		try {

			// Get a generator from the pool
			generator = pool.getWait();

			// Update global and engine counters
			LibMetrics.instance().hitCounter("render", "count");
			LibMetrics.instance().hitCounter("render", "type", type, "count");

			// Run implementation specific validations
			generator.onValidateRequest(request);

			// Render new image and get the bytes
			byte[] png = generator.onRender(request);

			// Calculate run time and log generation
			int time = (int) (System.currentTimeMillis() - start);
			LibLog.clogF("barcode", "I0601", type, data, png.length, time);

			// Create the object to be cached
			barcode = new CachedBarcode(type, data, png);

			// Add to cache if allowed
			if (request.useCache()) {
				ObjectCache.getCache(type).put(data, barcode);
			}

		} catch (GenerationException e) {

			// Update global and engine counters
			LibMetrics.instance().hitCounter("render", "invalid");
			LibMetrics.instance().hitCounter("render", "type", type, "invalid");

			// Pass it up
			throw e;

		} catch (LibObjectPoolerException e) {

			// Update global and engine counters
			LibMetrics.instance().hitCounter("render", "busy");
			LibMetrics.instance().hitCounter("render", "type", type, "busy");

			// Failed if unable to get generator from pool
			throw new GenerationException(ExceptionType.BUSY);

		} catch (Exception | Error e) {

			// Update global and engine counters
			LibMetrics.instance().hitCounter("render", "fail");
			LibMetrics.instance().hitCounter("render", "type", type, "fail");

			// Generation itself failed
			throw new GenerationException(ExceptionType.FAILED);
		} finally {

			// Update global and engine counters
			int time = (int) (System.currentTimeMillis() - start);
			LibMetrics.instance().hitCounter(time, "render", "time");
			LibMetrics.instance().hitCounter(time, "render", "type", type, "time");

			// Release the generator back to the pool
			if (generator != null) {
				pool.release(generator);
			}
		}

		// Return the barcode
		return barcode;
	}
}
