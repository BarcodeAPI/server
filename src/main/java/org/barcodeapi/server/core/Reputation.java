package org.barcodeapi.server.core;

import java.io.Serializable;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.json.JSONObject;

/**
 * Reputation.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class Reputation implements Serializable {

	private static final long serialVersionUID = 20260503L;

	private static final double REP_INITIAL;

	private static final double REP_ABUSE_LEVEL;

	private static final double REP_CAP_OKAY;
	private static final double REP_CAP_SPAM;

	private static final double REP_D_OKAY;
	private static final double REP_D_SPAM;

	private static final double REP_DECAY_OKAY;
	private static final double REP_DECAY_SPAM;

	static {

		JSONObject cfg = Config.get(Cfg.App)//
				.getJSONObject("reputation");

		REP_INITIAL = cfg.getDouble("initial");
		REP_ABUSE_LEVEL = cfg.getDouble("abuse_level");

		REP_CAP_OKAY = cfg.getDouble("cap_okay");
		REP_CAP_SPAM = cfg.getDouble("cap_spam");

		REP_D_OKAY = cfg.getDouble("req_okay");
		REP_D_SPAM = cfg.getDouble("req_spam");

		REP_DECAY_OKAY = cfg.getDouble("decay_okay");
		REP_DECAY_SPAM = cfg.getDouble("decay_spam");
	}

	private final boolean enforce;

	private double reputation;

	private long updated;

	public Reputation(boolean enforce) {
		this.enforce = enforce;
		this.reputation = REP_INITIAL;
		this.updated = System.currentTimeMillis();
	}

	public boolean enforced() {
		return enforce;
	}

	public double value() {
		return reputation;
	}

	public boolean isAbuser() {
		return ((REP_ABUSE_LEVEL >= reputation) && enforce);
	}

	public void update(boolean valid) {

		// Calculate new spam-detect value
		change((valid) ? REP_D_OKAY : REP_D_SPAM);
	}

	public double decay() {

		synchronized (this) {

			// Time since last update
			long dT = (System.currentTimeMillis() - updated);

			// Decay down to initial level
			if ((reputation > (REP_INITIAL * 1.1))) {
				return change((dT * REP_DECAY_OKAY));
			} else //

			// Decay up to initial level
			if ((reputation < (REP_INITIAL * 0.9))) {
				return change((dT * REP_DECAY_SPAM));
			}

			return 0;
		}
	}

	private double change(double delta) {
		synchronized (this) {

			double spamStart = reputation;
			double spamAdjust = (spamStart + delta);

			// Cap at upper and lower bounds
			if (spamAdjust > REP_CAP_OKAY) {
				spamAdjust = REP_CAP_OKAY;
			} else//
			if (spamAdjust < REP_CAP_SPAM) {
				spamAdjust = REP_CAP_SPAM;
			}

			// Set the new spam-detect level
			reputation = spamAdjust;
			updated = System.currentTimeMillis();

			// Return the actual difference
			return (spamAdjust - spamStart);
		}
	}
}
