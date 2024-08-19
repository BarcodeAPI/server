package org.barcodeapi.server.cache;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;

/**
 * CachedLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedLimiter extends CachedObject {

	private static final long serialVersionUID = 1L;

	private static final int LIMITER_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("limiter").getInt("life");

	private static final double TOKENS_INITIAL = 0.5;

	private final boolean enforce;

	private final String caller;

	private final long tokenLimit;

	private final double tokensPerMilli;

	private long timeMinted;

	private double tokenCount;

	public CachedLimiter(boolean enforce, String caller, long requests) {
		this.setTimeout(LIMITER_LIFE, TimeUnit.MINUTES);

		this.enforce = enforce;
		this.caller = caller;
		this.tokenLimit = requests;

		this.timeMinted = System.currentTimeMillis();
		this.tokensPerMilli = (requests > 0) ? //
				((double) requests / (double) getTimeout()) : 0;
		this.tokenCount = (requests == -1) ? -1 : (requests * TOKENS_INITIAL);
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
		return tokenLimit;
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
		if (tokenCount == -1) {
			return 0;
		}

		synchronized (this) {

			// Calculate time since last request
			long timeNow = System.currentTimeMillis();
			long timeSinceLastMint = (timeNow - getTimeLastMinted());

			// Generate new tokens based on user delay
			double tokensMinted = (timeSinceLastMint * this.tokensPerMilli);

			// Add minted count to existing count
			double newTokenCount = (this.tokenCount + tokensMinted);

			// Cap maximum number of tokens
			newTokenCount = ((newTokenCount > this.tokenLimit) ? this.tokenLimit : newTokenCount);

			// Update the token count
			this.timeMinted = timeNow;
			this.tokenCount = newTokenCount;

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
		return timeMinted;
	}

	/**
	 * Returns the current number of tokens the caller has.
	 * 
	 * @return
	 */
	public double numTokens() {
		return tokenCount;
	}

	/**
	 * Spend a number of tokens.
	 * 
	 * @param count number of tokens to spend
	 * @return token spend successful
	 */
	public boolean spendTokens(double count) {

		// Unlimited
		if (tokenCount == -1) {
			return true;
		}

		synchronized (this) {

			// If costs more
			if (count >= tokenCount) {

				// Return based on enforcement
				return (!isEnforced());
			}

			// Set new token count
			this.tokenCount = (tokenCount - count);

			// Return true if spent
			return true;
		}
	}
}
