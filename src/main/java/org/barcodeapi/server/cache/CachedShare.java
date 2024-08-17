package org.barcodeapi.server.cache;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;
import org.json.JSONArray;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

/**
 * CachedSession.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedShare extends CachedObject {

	private static final long serialVersionUID = 1L;

	private static final int OBJECT_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("share").getInt("life");

	private final String data;

	private final String hash;

	public CachedShare(JSONArray requests) {
		this.setTimeout(OBJECT_LIFE, TimeUnit.MINUTES);

		this.data = requests.toString();

		this.hash = LibExtrasHashes.sumMD5(data);
	}

	public String getData() {

		this.touch();
		return data;
	}

	public String getHash() {

		return hash;
	}
}
