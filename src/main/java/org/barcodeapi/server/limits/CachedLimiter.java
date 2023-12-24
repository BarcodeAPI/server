package org.barcodeapi.server.limits;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.server.core.AppConfig;
import org.barcodeapi.server.core.CachedObject;
import org.json.JSONObject;

public class CachedLimiter extends CachedObject {

	private static final JSONObject conf = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("limiter");

	private final String caller;

	private final long requests;

	private final double tpms;

	private double tokens;

	private long minted = 0;

	public CachedLimiter(String caller, long requests) {
		this.setTimeout(conf.getInt("life"), TimeUnit.MINUTES);

		this.caller = caller;
		this.requests = requests;
		this.tokens = (requests * 0.5);
		this.minted = System.currentTimeMillis();
		this.tpms = (requests > 0) ? ((double) requests / (double) getTimeout()) : 0;
	}

	public String getCaller() {
		return caller;
	}

	public long getLimit() {
		return requests;
	}

	public double mintTokens() {

		synchronized (this) {

			// Calculate time since last request
			long timeNow = System.currentTimeMillis();
			long timeSinceLastMint = (timeNow - getTimeLastMinted());

			// Generate new tokens based on user delay
			double tokensMinted = (timeSinceLastMint * this.tpms);

			// Add minted count to existing count
			double newTokenCount = this.tokens + tokensMinted;

			// Cap maximum number of tokens
			newTokenCount = (newTokenCount > this.requests) ? this.requests : newTokenCount;

			// Update the token count
			this.minted = timeNow;
			this.tokens = newTokenCount;

			return tokensMinted;
		}
	}

	public long getTimeLastMinted() {
		return minted;
	}

	public double numTokens() {
		return tokens;
	}

	public boolean spendTokens(double count) {

		// Unlimited
		if (tokens == -1) {
			return true;
		}

		synchronized (this) {

			// Set new token count
			this.tokens = (tokens > count) ? (tokens - count) : 0;

			// Return if has tokens
			return this.tokens > 0;
		}
	}
}
