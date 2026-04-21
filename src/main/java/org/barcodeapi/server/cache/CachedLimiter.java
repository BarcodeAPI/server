package org.barcodeapi.server.cache;

import org.barcodeapi.server.core.Reputation;
import org.barcodeapi.server.core.Tokens;
import org.json.JSONObject;

/**
 * CachedLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class CachedLimiter extends CachedObject {

	private static final long serialVersionUID = 20241222L;

	private final String caller;

	private final Reputation reputation;

	private final Tokens tokens;

	public CachedLimiter(boolean enforce, String caller, long requests) {
		super("limiter");

		this.caller = caller;

		this.reputation = new Reputation();

		this.tokens = new Tokens(enforce, requests);
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
	public String getCaller() {
		return caller;
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
	public boolean userRequest(boolean valid, double cost) {

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
				.put("caller", getCaller())//
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
