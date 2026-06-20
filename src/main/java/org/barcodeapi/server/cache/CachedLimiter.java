package org.barcodeapi.server.cache;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.barcodeapi.server.core.Reputation;
import org.barcodeapi.server.core.Tokens;
import org.json.JSONObject;

/**
 * CachedLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class CachedLimiter extends CachedObject {

	// Default values for new limiters
	private static final int DEFLIMIT_RATE;
	private static final boolean DEFLIMIT_ENFORCE;

	static {

		// Load plan from configuration
		JSONObject freePlan = Config//
				.get(Cfg.Plans).getJSONObject("free");

		// Free plan defaults
		DEFLIMIT_RATE = freePlan.getInt("limit");
		DEFLIMIT_ENFORCE = freePlan.getBoolean("enforce");
	}

	private static final long serialVersionUID = 20260503L;

	private final String callerID;

	private final Reputation reputation;

	private final Tokens tokens;

	public CachedLimiter(Subscriber sub, String address) {
		super("limiter");

		// Assign the userID
		this.callerID = (sub != null) ? sub.getCustomer() : address;

		// Determine token limits for the limiter
		int limit = (sub != null) ? sub.getLimit() : DEFLIMIT_RATE;
		boolean enforce = (sub != null) ? sub.isEnforced() : DEFLIMIT_ENFORCE;
		this.tokens = new Tokens(enforce, limit);

		// Setup user reputation tracker
		boolean repBlock = (sub != null) ? false : enforce;
		this.reputation = new Reputation(repBlock);
	}

	public void touch(CachedSession session) {
		super.touch();

		if (session != null) {

		}
	}

	/**
	 * Short lived and can be cleaned if token balance is full.
	 * 
	 * @return the object is short lived
	 */

	@Override
	public boolean isShortLived() {
		return (!reputation.isAbuser() && //
				(tokens.getCount() == tokens.getLimit()));
	}

	/**
	 * Returns the caller associated with the limiter.
	 * 
	 * @return caller
	 */
	public String getCallerID() {
		return callerID;
	}

	/**
	 * Returns the Reputation object.
	 * 
	 * @return the reputation object
	 */
	public Reputation getReputation() {
		return reputation;
	}

	/**
	 * Returns the Tokens controller object.
	 * 
	 * @return the tokens controller object
	 */
	public Tokens getTokens() {
		return tokens;
	}

	/**
	 * Called to handle a user request.
	 * 
	 * Spends tokens and handles reputation.
	 * 
	 * @param valid request was valid
	 * @param cost  cost of the request
	 */
	public boolean onRequest(boolean valid, double cost) {

		// Update user reputation
		reputation.update(valid);

		// Spend rate limit tokens
		return tokens.spend(cost);
	}

	/**
	 * Returns the rate limiter object as a JSON object.
	 * 
	 * @return the rate limiter object in JSON format
	 */
	public JSONObject asJSON() {

		return (new JSONObject()//
				.put("caller", getCallerID())//
				.put("requests", getAccessCount())//
				.put("reputation", reputation.value())//
				.put("time", new JSONObject() //
						.put("created", getTimeCreated())//
						.put("expires", getTimeExpires())//
						.put("last", getTimeLastTouched()))//
				.put("tokens", new JSONObject()//
						.put("enforce", tokens.isEnforced())//
						.put("limit", tokens.getLimit())//
						.put("count", tokens.getCountStr())//
						.put("spend", tokens.getTotalSpend())//
						.put("minted", tokens.getTimeLastMinted())));
	}
}
