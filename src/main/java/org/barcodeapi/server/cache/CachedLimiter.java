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
		if (tokenCount == -1) {
			return 0;
		}

		synchronized (this) {
			long timeNow = System.currentTimeMillis();
			double oldCount = tokenCount;

			double minted = (timeNow - timeMinted) * tokensPerMilli;

			// new count, capped at limit
			double newCount = Math.min(tokenLimit, (oldCount + minted));

			// update limiter
			this.tokenCount = newCount;
			this.timeMinted = timeNow;

			// return number of tokens added
			return (newCount - oldCount);
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
	 * Returns the current number of tokens the caller has.
	 * 
	 * @return number of tokens the caller has
	 */
	public String getTokenCountStr() {
		return String.format("%.2f", tokenCount);
	}

	/**
	 * Returns true if limiter is unlimited.
	 * 
	 * @return true if limiter is unlimited
	 */
	public boolean isUnlimited() {
		return (tokenCount == -1);
	}

	/**
	 * Returns the number of tokens spent.
	 * 
	 * @return number of tokens spent
	 */
	public double getTokenSpend() {
		return tokenSpend;
	}

	public boolean checkBalance(double cost) {

		// We have tokens and cost is more then we have
		if ((this.tokenCount > 0) && (cost > this.tokenCount)) {

			// Return based on enforcement
			return (!isEnforced());
		}

		// Balance allows spend
		return true;
	}

	/**
	 * Spend a number of tokens.
	 * 
	 * @param cost number of tokens to spend
	 * @return token spend successful
	 */
	public boolean spendTokens(double cost) {

		// Fail if spending negative
		if (cost < 0) {
			throw new IllegalArgumentException(//
					"Token count cannot be less then 0.");
		}

		// Allow if unlimited balance
		if (isUnlimited()) {
			return true;
		}

		synchronized (this) {

			// Check existing balance
			if (!checkBalance(cost)) {
				return false;
			}

			// Set new token counts
			this.tokenSpend += cost;
			this.tokenCount -= cost;

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
