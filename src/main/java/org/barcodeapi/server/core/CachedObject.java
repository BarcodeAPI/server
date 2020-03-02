package org.barcodeapi.server.core;

import java.util.concurrent.TimeUnit;

public abstract class CachedObject {

	private long timeTimeout;
	private long timeTouched;

	public CachedObject() {

		this.touch();
		this.setTimeout(60, TimeUnit.MINUTES);
	}

	public long getTimeout() {
		return this.timeTimeout;
	}

	public void setTimeout(long timeoutTime, TimeUnit timeoutUnit) {
		this.timeTimeout = TimeUnit.MILLISECONDS.convert(timeoutTime, timeoutUnit);
	}

	protected void touch() {
		timeTouched = System.currentTimeMillis();
	}

	public boolean isExpired() {
		return ((timeTouched + timeTimeout) < System.currentTimeMillis());
	}
}
