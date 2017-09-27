package org.barcodeapi.server.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.statistics.StatsCollector;

public class ImageCache {

	private static ImageCache imageCache;

	private ConcurrentHashMap<CodeType, ConcurrentHashMap<String, byte[]>> cache;

	public ImageCache() {

		cache = new ConcurrentHashMap<CodeType, ConcurrentHashMap<String, byte[]>>();
	}

	public void createCache(CodeType type) {

		cache.put(type, new ConcurrentHashMap<String, byte[]>());
	}

	public byte[] getImage(CodeType type, String data) {

		if (cache.get(type).containsKey(data)) {

			String counterName = "cache." + type.toString() + ".hit";
			StatsCollector.getInstance().incrementCounter(counterName);
		}

		return cache.get(type).get(data);
	}

	public void addImage(CodeType type, String data, byte[] image) {

		StatsCollector.getInstance().incrementCounter("cache.add");

		cache.get(type).put(data, image);
	}

	public void removeImage(CodeType type, String data) {

		StatsCollector.getInstance().incrementCounter("cache.remove");

		cache.get(type).remove(data);
	}

	public static synchronized ImageCache getInstance() {

		if (imageCache == null) {

			imageCache = new ImageCache();
		}
		return imageCache;
	}
}
