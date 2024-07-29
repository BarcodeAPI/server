package org.barcodeapi.server.limits;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.core.CachedObject;

/**
 * CachedLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedLimiter extends CachedObject {

	private static final int LIMITER_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("limiter").getInt("life");

	private final String caller;

	private final long requests;

	private final boolean enforce;

	private final double tpms;

	private double tokens;

	private long minted = 0;

	public CachedLimiter(String caller, long requests, boolean enforce) {
		this.setTimeout(LIMITER_LIFE, TimeUnit.MINUTES);

		this.enforce = enforce;
		this.caller = caller;
		this.requests = requests;
		this.tokens = (requests == -1) ? -1 : (requests * 0.5);
		this.minted = System.currentTimeMillis();
		this.tpms = (requests > 0) ? ((double) requests / (double) getTimeout()) : 0;
	}

	/**
	 * Returns the caller associated with the limiter.
	 * 
	 * @return caller
	 */
	public String getCaller() {
		return caller;
	}

	/**
	 * Returns the limit of the limiter.
	 * 
	 * @return rate limit
	 */
	public long getLimit() {
		return requests;
	}

	/**
	 * Returns true if the limiter should be enforced.
	 * 
	 * @return limiter enforced
	 */
	public final boolean isEnforced() {
		return enforce;
	}

	/**
	 * Mints new tokens for the limiter.
	 * 
	 * Minted count is based on the limiter's request limit and the time since the
	 * previous minting.
	 * 
	 * @return number of tokens minter
	 */
	public double mintTokens() {

		// Unlimited
		if (tokens == -1) {
			return 0;
		}

		synchronized (this) {

			// Calculate time since last request
			long timeNow = System.currentTimeMillis();
			long timeSinceLastMint = (timeNow - getTimeLastMinted());

			// Generate new tokens based on user delay
			double tokensMinted = (timeSinceLastMint * this.tpms);

			// Add minted count to existing count
			double newTokenCount = (this.tokens + tokensMinted);

			// Cap maximum number of tokens
			newTokenCount = ((newTokenCount > this.requests) ? this.requests : newTokenCount);

			// Update the token count
			this.minted = timeNow;
			this.tokens = newTokenCount;

			// Return tokens minted
			return tokensMinted;
		}
	}

	/**
	 * Returns the time at which tokens were last minted.
	 * 
	 * @return time tokens last minted
	 */
	public long getTimeLastMinted() {
		return minted;
	}

	/**
	 * Returns the current number of tokens the caller has.
	 * 
	 * @return
	 */
	public double numTokens() {
		return tokens;
	}

	/**
	 * Spend a number of tokens.
	 * 
	 * @param count number of tokens to spend
	 * @return token spend successful
	 */
	public boolean spendTokens(double count) {

		// Unlimited
		if (tokens == -1) {
			return true;
		}

		synchronized (this) {

			// Set new token count
			this.tokens = (tokens > count) ? (tokens - count) : 0;

			// Return if has tokens based on enforcement
			return (this.tokens > 0) || !isEnforced();
		}
	}
}
