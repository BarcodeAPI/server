package org.barcodeapi.server.core;

/**
 * Tokens.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class Tokens {

	private static final double _DAY = (24 * 60 * 60 * 1000);

	private final boolean enforce;

	private double tokenSpend;

	private double tokenCount;

	private final long tokenLimit;

	private long timeMinted;

	private final double tokensPerMilli;

	public Tokens(boolean enforce, long requests) {

		this.enforce = enforce;

		this.tokenSpend = 0;
		this.tokenCount = requests;
		this.tokenLimit = requests;

		this.timeMinted = System.currentTimeMillis();
		this.tokensPerMilli = (requests > 0) ? ((double) requests / _DAY) : 0;
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
	 * Returns the limit of the limiter.
	 * 
	 * @return rate limit
	 */
	public long getLimit() {
		return tokenLimit;
	}

	/**
	 * Mints new tokens for the limiter.
	 * 
	 * Minted count is based on the limiter's request limit and the time since the
	 * previous minting.
	 * 
	 * @return number of tokens minter
	 */
	public double mint() {

		// Skip if already at limit, or unlimited
		if ((tokenCount == tokenLimit) || (tokenCount == -1)) {
			return 0;
		}

		// Lock the limiter
		synchronized (this) {

			// Determine time since last update
			long timeNow = System.currentTimeMillis();
			long timeDelta = (timeNow - timeMinted);

			// Determine number of tokens minted, add to current, cap at limit
			double oldCount = tokenCount;
			double minted = (timeDelta * tokensPerMilli);
			double newCount = Math.min(tokenLimit, (oldCount + minted));

			// Update the limiter
			tokenCount = newCount;
			timeMinted = timeNow;

			// Return number of tokens added
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
	public double getCount() {
		return tokenCount;
	}

	/**
	 * Returns the current number of tokens the caller has.
	 * 
	 * @return number of tokens the caller has
	 */
	public String getCountStr() {
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
	public double getTotalSpend() {
		return tokenSpend;
	}

	/**
	 * Returns true if the limiter has less then 5% of it's token balance remaining.
	 * 
	 * @return limiter has a low token balance
	 */
	public boolean isLowBalance() {

		return (!isUnlimited() && ((0.05 * tokenLimit) > tokenCount));
	}

	/**
	 * Returns true if the token balance allows the cost spend.
	 * 
	 * @param cost the cost of the request
	 * @return true if balance allows spend
	 */
	public boolean allowSpend(double cost) {

		// Allow if balance exceeds cost or unlimited
		return ((tokenCount > cost) || isUnlimited());
	}

	/**
	 * Spend a number of tokens.
	 *
	 * @param cost number of tokens to spend
	 * @return token spend successful
	 */
	public boolean spend(double cost) {

		// Fail if spending negative
		if (cost < 0) {
			throw new IllegalArgumentException(//
					"Token cost cannot be negative.");
		}

		synchronized (this) {

			// Unlimited token balance
			if (tokenCount == -1) {
				tokenSpend += cost;
				return true;
			}

			// Limiter has tokens, spend them
			if (tokenCount >= cost) {
				tokenSpend += cost;
				tokenCount -= cost;
				return true;
			}

			// Limiter out of tokens, not enforced
			if (!isEnforced()) {
				tokenSpend += cost;
				return true;
			}

			// Rate limit reached
			return false;
		}
	}
}
