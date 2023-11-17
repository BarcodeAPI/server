package org.barcodeapi.server.limits;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.server.core.AppConfig;
import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.RateLimitException;
import org.json.JSONObject;

public class CachedLimiter extends CachedObject {

	private static final JSONObject conf = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("limiter");

	private final String caller;

	private final long requests;

	private final double tpms;

	private double tokens;

	public CachedLimiter(String caller, long requests) {
		this.setTimeout(conf.getInt("life"), TimeUnit.MINUTES);

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
		return (tokens != 0);
	}

	public void spendTokens(double count) {

		// Unlimited
		if (tokens <= 0) {
			return;
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
			this.tokens = (newTokenCount > count) ? (newTokenCount - count) : 0;
			this.touch();

			// Return if has tokens
			if (this.tokens == 0) {
				throw new RateLimitException();
			}
		}
	}
}
