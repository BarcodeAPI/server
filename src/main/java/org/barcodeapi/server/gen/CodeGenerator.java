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

		// validate code format
		if (!data.matches(getType().getFormatPattern())) {

			throw new IllegalArgumentException("Invalid data for selected code type");
		}

		// any additional generator validations
		onValidateRequest(data);

		// increment counter
		String counterName = "render." + getType().toString() + ".hit";
		StatsCollector.getInstance().incrementCounter(counterName);

		// time and render
		long timeStart = System.currentTimeMillis();
		byte[] image = onRender(data);
		double time = System.currentTimeMillis() - timeStart;

		// increment counter
		counterName = "render." + getType().toString() + ".time";
		StatsCollector.getInstance().incrementCounter(counterName, time);

		// return image
		return image;
	}

	public abstract void onValidateRequest(String data);

	public abstract byte[] onRender(String data);
}
