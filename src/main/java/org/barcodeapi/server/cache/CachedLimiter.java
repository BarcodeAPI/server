package org.barcodeapi.server.cache;

import org.json.JSONObject;

/**
 * CachedLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedLimiter extends CachedObject {

	private static final long serialVersionUID = 20241222L;

	private static final double _DAY = (24 * 60 * 60 * 1000);

	private final boolean enforce;

	private final String caller;

	private double tokenSpend;

	private double tokenCount;

	private final long tokenLimit;

	private long timeMinted;

	private final double tokensPerMilli;

	public CachedLimiter(boolean enforce, String caller, long requests) {
		super("limiter");

		this.enforce = enforce;
		this.caller = caller;

		this.tokenSpend = 0;
		this.tokenCount = requests;
		this.tokenLimit = requests;

		this.timeMinted = System.currentTimeMillis();
		this.tokensPerMilli = (requests > 0) ? ((double) requests / _DAY) : 0;
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
	public long getTokenLimit() {
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
	 * @return number of tokens the caller has
	 */
	public double getTokenCount() {
		return tokenCount;
	}

	/**
	 * Returns the number of tokens spent.
	 * 
	 * @return number of tokens spent
	 */
	public double getTokenSpend() {
		return tokenSpend;
	}

	/**
	 * Spend a number of tokens.
	 * 
	 * @param count number of tokens to spend
	 * @return token spend successful
	 */
	public boolean spendTokens(double count) {

		// Unlimited
		if (this.tokenCount == -1) {
			return true;
		}

		synchronized (this) {

			// If cost is more then have
			if (count > this.tokenCount) {

				// Return based on enforcement
				return (!isEnforced());
			}

			// Set new token count
			this.tokenSpend += count;
			this.tokenCount -= count;

			// Return true if spent
			return true;
		}
	}

	/**
	 * Returns the rate limiter object as a JSON object.
	 * 
	 * @return the rate limiter object in JSON format
	 */
	public JSONObject asJSON() {

		return (new JSONObject()//
				.put("caller", getCaller())//
				.put("created", getTimeCreated())//
				.put("expires", getTimeExpires())//
				.put("last", getTimeLastTouched())//
				.put("enforce", isEnforced())//
				.put("tokenLimit", getTokenLimit())//
				.put("tokenCount", getTokenCount())//
				.put("tokenSpend", getTokenSpend()));
	}
}
