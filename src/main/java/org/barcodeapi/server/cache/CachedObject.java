package org.barcodeapi.server.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * CachedObject.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public abstract class CachedObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long timeCreated;
	private long timeTimeout;
	private long timeTouched;
	private long accessCount;

	public CachedObject() {

		this.accessCount = 0;
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

	public long getAccessCount() {
		return accessCount;
	}

	public void touch() {
		accessCount += 1;
		timeTouched = System.currentTimeMillis();
	}

	public boolean isExpired() {
		return ((timeTouched + timeTimeout) < System.currentTimeMillis());
	}
}
