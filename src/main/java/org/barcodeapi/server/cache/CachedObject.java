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

	protected CachedObject() {

		this.accessCount = 0;
		this.timeCreated = System.currentTimeMillis();

		this.touch();
		this.setTimeout(60, TimeUnit.MINUTES);
	}

	/**
	 * Returns the time the object will stay cached, after it's last access, until
	 * it will be flushed.
	 * 
	 * @return object timeout
	 */
	public long getTimeout() {
		return this.timeTimeout;
	}

	/**
	 * Returns the time the object was initially created.
	 * 
	 * @return time object was created
	 */
	public long getTimeCreated() {
		return this.timeCreated;
	}

	/**
	 * Returns the time the object was last touched.
	 * 
	 * @return time when last touched
	 */
	public long getTimeLastTouched() {
		return this.timeTouched;
	}

	/**
	 * Return the time (in milliseconds) the object will expire.
	 * 
	 * @return time object will expire
	 */
	public long getTimeExpires() {
		return this.timeTouched + this.timeTimeout;
	}

	/**
	 * Update the timeout of the object.
	 * 
	 * @param timeoutTime timeout time
	 * @param timeoutUnit timeout time unit
	 */
	public void setTimeout(long timeoutTime, TimeUnit timeoutUnit) {
		this.timeTimeout = TimeUnit.MILLISECONDS.convert(timeoutTime, timeoutUnit);
	}

	/**
	 * Returns the number of time the object has been accessed.
	 * 
	 * @return number of touches
	 */
	public long getAccessCount() {
		return accessCount;
	}

	/**
	 * Touch the object.
	 * 
	 * Updates the access count and time.
	 */
	public void touch() {
		accessCount += 1;
		timeTouched = System.currentTimeMillis();
	}

	/**
	 * Returns true if the cached object is expired.
	 * 
	 * @return object is expired
	 */
	public boolean isExpired() {
		return ((timeTouched + timeTimeout) < System.currentTimeMillis());
	}
}
