package org.barcodeapi.server.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.barcodeapi.server.core.Reputation;
import org.barcodeapi.server.core.Tokens;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CachedLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class CachedLimiter extends CachedObject {

	// Serialization ID for caching
	private static final long serialVersionUID = 20260712L;

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

	private final String callerID;

	private final Tokens tokens;

	private final Reputation reputation;

	private final ConcurrentHashMap<String, Integer> sessionIPs;
	private final ConcurrentHashMap<String, Integer> sessionKeys;

	public CachedLimiter(Subscriber sub, String address) {
		super("limiter");

		// Assign the userID (based on subscriber or IP)
		this.callerID = (sub != null) ? sub.getCustomer() : address;

		// Determine token limits for the limiter
		int limit = (sub != null) ? sub.getLimit() : DEFLIMIT_RATE;
		boolean enforce = (sub != null) ? sub.isEnforced() : DEFLIMIT_ENFORCE;
		this.tokens = new Tokens(enforce, limit);

		// Setup user reputation tracker
		boolean repBlock = (sub != null) ? false : enforce;
		this.reputation = new Reputation(repBlock);

		// Memory map for limiter session history
		this.sessionIPs = new ConcurrentHashMap<>();
		this.sessionKeys = new ConcurrentHashMap<>();
	}

	public void touch(String ip, CachedSession session) {
		super.touch();

		int countIP = sessionIPs.containsKey(ip) ? sessionIPs.get(ip) : 0;
		sessionIPs.put(ip, (countIP + 1));

		if (session != null) {
			String key = session.getKey();
			int countSes = sessionKeys.containsKey(key) ? sessionKeys.get(key) : 0;
			sessionKeys.put(key, (countSes + 1));
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
	 * Returns the limiter session history as a JSON object.
	 * 
	 * @return the limiter session history in JSON format
	 */
	public JSONObject getHistroy() {

		JSONArray ipList = new JSONArray();
		for (Map.Entry<String, Integer> entry : sessionIPs.entrySet()) {

			ipList.put(new JSONObject() //
					.put("ip", entry.getKey())//
					.put("hits", entry.getValue()));
		}

		JSONArray keyList = new JSONArray();
		for (Map.Entry<String, Integer> entry : sessionKeys.entrySet()) {

			keyList.put(new JSONObject() //
					.put("key", entry.getKey())//
					.put("hits", entry.getValue()));
		}

		return (new JSONObject())//
				.put("ips", ipList)//
				.put("keys", keyList);
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
				.put("reputation", getReputation().value())//
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
