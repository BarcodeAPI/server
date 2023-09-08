package org.barcodeapi.server.cache;

import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.ObjectCache;
import org.barcodeapi.server.gen.CodeType;

import com.mclarkdev.tools.libmetrics.LibMetrics;

public class BarcodeCache {

	public static ObjectCache getCache(CodeType type) {
		return ObjectCache.getCache(type.toString());
	}

	public static CachedBarcode getBarcode(CodeType type, String data) {
		LibMetrics.hitMethodRunCounter();

		CachedObject o = getCache(type).get(data);
		if (o == null) {
			return null;
		}

		return (CachedBarcode) o;
	}

	public static void addBarcode(CodeType type, String data, CachedBarcode object) {
		LibMetrics.hitMethodRunCounter();

		getCache(type).put(data, object);
	}
}
