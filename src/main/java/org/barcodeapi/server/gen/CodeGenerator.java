package org.barcodeapi.server.gen;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.statistics.StatsCollector;

public abstract class CodeGenerator {

	private final CodeType codeType;

	public CodeGenerator(CodeType type) {

		this.codeType = type;

		BarcodeCache.getInstance().createCache(type);
	}

	public CodeType getType() {

		return codeType;
	}

	public byte[] getCode(String data) {

		// Validate data format
		if (!data.matches(getType().getExtendedPattern())) {

			throw new IllegalArgumentException("Invalid data for selected code type");
		}

		onValidateRequest(data);

		String counterName = "render." + getType().toString() + ".hit";
		StatsCollector.getInstance().incrementCounter(counterName);

		long timeStart = System.currentTimeMillis();
		byte[] image = onRender(data);
		double time = System.currentTimeMillis() - timeStart;

		counterName = "render." + getType().toString() + ".time";
		StatsCollector.getInstance().incrementCounter(counterName, time);

		return image;
	}

	public abstract void onValidateRequest(String data);

	public abstract byte[] onRender(String data);
}
