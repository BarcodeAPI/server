package org.barcodeapi.server.core;

import java.util.concurrent.TimeUnit;

public abstract class CachedObject {

	private final long timeCreated;
	private long timeTimeout;
	private long timeTouched;

	public CachedObject() {

		this.timeCreated = System.currentTimeMillis();

		this.touch();
		this.setTimeout(60, TimeUnit.MINUTES);
	}

	public long getTimeout() {
		return this.timeTimeout;
	}

	public long getTimeCreated() {
		return this.timeCreated;
	}

	public long getTimeLastSeen() {
		return this.timeTouched;
	}

	public long getTimeExpires() {
		return this.timeTouched + this.timeTimeout;
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
