package org.barcodeapi.server.cache;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CodeType;
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

	private final CodeType type;
	private final byte[] data;

	private final String strRaw;
	private final String strNice;
	private final String strEncoded;

	public CachedBarcode(CodeType type, String raw, byte[] data) {
		this.setTimeout(OBJECT_LIFE, TimeUnit.MINUTES);

		this.type = type;
		this.data = data;

		this.strRaw = raw;
		this.strNice = CodeUtils.stripIllegal(raw);
		this.strEncoded = CodeUtils.encodeURL(raw);
	}

	public CodeType getBarcodeType() {

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

		return data == null ? 0 : data.length;
	}

	public String encodeBase64() {

		return Base64.getEncoder()//
				.encodeToString(getBarcodeData());
	}

	public String encodeJSON() {

		return ((new JSONObject()//
				.put("type", getBarcodeType().getName()))//
				.put("encoded", getBarcodeStringEncoded())//
				.put("b64", encodeBase64())//
		).toString(4);
	}
}
