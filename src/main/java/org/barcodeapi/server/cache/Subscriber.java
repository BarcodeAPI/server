package org.barcodeapi.server.cache;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CachedSession.java
 * 
 * A user session object allowing for the detailed tracking of individual user
 * usage activities across all handlers.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Subscriber {

	private final String customer;
	private final long subscribed;
	private final boolean active;
	private final boolean enforce;
	private final int limit;

	private final JSONArray ips;
	private final JSONArray keys;

	public Subscriber(JSONObject def) {

		this.customer = def.getString("customer");
		this.subscribed = def.getLong("subscribed");
		this.active = def.getBoolean("active");
		this.enforce = def.getBoolean("enforce");
		this.limit = def.getInt("limit");
		this.ips = def.getJSONArray("ips");
		this.keys = def.getJSONArray("keys");
	}

	public String getCustomer() {
		return this.customer;
	}

	public long getSubscribed() {
		return this.subscribed;
	}

	public boolean getActive() {
		return this.active;
	}

	public boolean getEnforce() {
		return this.enforce;
	}

	public int getLimit() {
		return this.limit;
	}

	public JSONArray getIPs() {
		return this.ips;
	}

	public JSONArray getKeys() {
		return this.keys;
	}

	/**
	 * Returns the user session as a JSON object.
	 * 
	 * @return the user session in JSON format
	 */
	public JSONObject asJSON() {

		return new JSONObject() //
				.put("customer", getCustomer())//
				.put("subscribed", getSubscribed())//
				.put("active", getActive())//
				.put("enforce", getEnforce())//
				.put("limit", getLimit())//
				.put("ips", getIPs())//
				.put("keys", getKeys());
	}
}
