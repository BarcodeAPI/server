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
	private final int batch;

	private final JSONArray ips;
	private final JSONArray keys;
	private final JSONArray apps;

	public Subscriber(JSONObject def) {

		// Basic subscriber details
		this.customer = def.getString("customer");
		this.subscribed = def.getLong("subscribed");

		// Account active / enforced
		this.active = def.getBoolean("active");
		this.enforce = def.getBoolean("enforce");

		// Account limits
		this.limit = def.getInt("limit");
		this.batch = def.getInt("batch");

		// Account associations
		this.ips = def.optJSONArray("ips", new JSONArray());
		this.keys = def.optJSONArray("keys", new JSONArray());
		this.apps = def.optJSONArray("apps", new JSONArray());
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

	public boolean isEnforced() {
		return this.enforce;
	}

	public int getLimit() {
		return this.limit;
	}

	public int getMaxBatch() {
		return this.batch;
	}

	public JSONArray getIPs() {
		return this.ips;
	}

	public JSONArray getKeys() {
		return this.keys;
	}

	public JSONArray getApps() {
		return this.apps;
	}

	/**
	 * Returns the subscriber info as a JSON object.
	 * 
	 * @return the subscriber info in JSON format
	 */
	public JSONObject asJSON() {

		return new JSONObject() //
				.put("customer", getCustomer())//
				.put("subscribed", getSubscribed())//
				.put("active", getActive())//
				.put("enforce", isEnforced())//
				.put("limit", getLimit())//
				.put("batch", getMaxBatch())//
				.put("ips", getIPs())//
				.put("keys", getKeys())//
				.put("apps", getApps());
	}
}
