package org.barcodeapi.server.cache;

import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.gen.BarcodeRequest;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * BarcodeCache.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeCache {

	public static CachedBarcode getBarcode(BarcodeRequest request) {
		LibMetrics.hitMethodRunCounter();

		CachedObject o = ObjectCache.getCache(request.getType().getName()).get(request.getData());

		return ((o == null) ? null : ((CachedBarcode) o));
	}

	public static void addBarcode(String type, String data, CachedBarcode object) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache.getCache(type).put(data, object);
	}
}
