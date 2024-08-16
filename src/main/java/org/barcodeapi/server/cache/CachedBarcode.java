package org.barcodeapi.server.cache;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.core.utils.CodeUtils;
import org.json.JSONObject;

/**
 * CachedBarcode.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedBarcode extends CachedObject {

	private static final long serialVersionUID = 1L;

	private static final int OBJECT_LIFE = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("barcode").getInt("life");

	private final String type;
	private final byte[] data;

	private final String strRaw;
	private final String strNice;
	private final String strEncoded;

	public CachedBarcode(String type, String raw, byte[] data) {

		// Fail if null data
		if (type == null || data == null) {
			throw new IllegalArgumentException();
		}

		this.type = type;
		this.data = data;

		this.strRaw = raw;
		this.strNice = CodeUtils.stripIllegal(raw);
		this.strEncoded = CodeUtils.encodeURL(raw);

		this.setTimeout(OBJECT_LIFE, TimeUnit.MINUTES);
	}

	public String getBarcodeType() {

		return type;
	}

	public byte[] getBarcodeData() {

		this.touch();
		return data;
	}

	public String getBarcodeStringRaw() {

		return strRaw;
	}

	public String getBarcodeStringNice() {

		return strNice;
	}

	public String getBarcodeStringEncoded() {

		return strEncoded;
	}

	public int getBarcodeDataSize() {

		return data.length;
	}

	public String encodeBase64() {

		return Base64.getEncoder()//
				.encodeToString(getBarcodeData());
	}

	public String encodeJSON() {

		return ((new JSONObject()//
				.put("type", getBarcodeType()))//
				.put("encoded", getBarcodeStringEncoded())//
				.put("b64", encodeBase64())//
		).toString(4);
	}
}
