package org.barcodeapi.server.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

/**
 * CachedShare.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedShare extends CachedObject {

	private static final long serialVersionUID = 1L;

	private static final int OBJECT_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("share").getInt("life");

	private final String data;

	private final int entries;

	private final String hash;

	/**
	 * Create a new share object from a given set of BarcodeRequest objects.
	 * 
	 * @param requests
	 */
	public CachedShare(List<BarcodeRequest> requests) {
		this.setTimeout(OBJECT_LIFE, TimeUnit.MINUTES);

		// Turn requests into JSON array
		JSONArray reqs = new JSONArray();
		for (BarcodeRequest r : requests) {
			reqs.put(r.encodeURI());
		}

		// Hash the data and save for later
		this.data = reqs.toString();
		this.entries = reqs.length();
		this.hash = LibExtrasHashes.sumMD5(data);
	}

	/**
	 * Returns the data contained in the share.
	 * 
	 * @return the data contained in the share
	 */
	public String getData() {

		this.touch();
		return data;
	}

	/**
	 * Returns the number of request entries in the share.
	 * 
	 * @return the number of request entries in the share
	 */
	public int getNumEntries() {

		return entries;
	}

	/**
	 * Returns the hash of the share data, this is used as the access key.
	 * 
	 * @return the hash of the share data
	 */
	public String getHash() {

		return hash;
	}

	/**
	 * Returns the share data encoded in a JSON object.
	 * 
	 * @return share data in JSON format
	 */
	public String encodeJSON() {

		return ((new JSONObject()) //
				.put("created", getTimeCreated())//
				.put("expires", getTimeExpires())//
				.put("accessed", getAccessCount())//
				.put("hash", getHash())//
				.put("data", getData())//
		).toString();
	}
}
