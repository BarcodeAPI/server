package org.barcodeapi.server.limits;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.server.core.CachedObject;

public class ClientLimiter extends CachedObject {

	private static final long _1D = 1000 * 60 * 60 * 24;

	private final String caller;

	private final long requests;

	private final double tpms;

	private double tokens;

	public ClientLimiter(String caller, long requests) {
		this.setTimeout(_1D, TimeUnit.MILLISECONDS);
		this.caller = caller;
		this.requests = requests;
		this.tokens = requests;
		this.tpms = (requests > 0) ? ((double) requests / (double) getTimeout()) : 0;
	}

	public String getCaller() {
		return caller;
	}

	public long getLimit() {
		return requests;
	}

	public double numTokens() {
		return tokens;
	}

	public boolean allowRequest() {

		// Unlimited
		if (tokens == -1) {
			return true;
		}

		synchronized (this) {

			// Calculate time since last request
			long timeSinceLast = (System.currentTimeMillis() - getTimeLastSeen());

			// Generate new tokens based on user delay
			double tokensGranted = (timeSinceLast * this.tpms);

			// Calculate the new token count with maximum
			double newTokenCount = this.tokens + tokensGranted;
			newTokenCount = (newTokenCount > this.requests) ? this.requests : newTokenCount;

			// Set new token count and touch the object
			this.tokens = (newTokenCount > 1) ? (newTokenCount - 1) : 0;
			this.touch();

			// Return if has tokens
			return (this.tokens > 0);
		}
	}
}
