package org.barcodeapi.server.cache;

import java.util.Base64;

import org.barcodeapi.core.utils.CodeUtils;
import org.json.JSONObject;

/**
 * CachedBarcode.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CachedBarcode extends CachedObject {

	private static final long serialVersionUID = 20241222L;

	private final String type;
	private final byte[] data;

	private final String strRaw;
	private final String strNice;
	private final String strEncoded;

	public CachedBarcode(String type, String raw, byte[] data) {
		super("barcode");

		// Fail if null data
		if (type == null || data == null) {
			throw new IllegalArgumentException();
		}

		this.type = type;
		this.data = data;

		this.strRaw = raw;
		this.strNice = CodeUtils.stripIllegal(raw);
		this.strEncoded = CodeUtils.encodeURL(raw);
	}

	/**
	 * Returns the name of the barcode type.
	 * 
	 * @return the name of the barcode type
	 */
	public String getBarcodeType() {

		return type;
	}

	/**
	 * Returns the barcode image data.
	 * 
	 * @return the barcode image data
	 */
	public byte[] getBarcodeData() {

		this.touch();
		return data;
	}

	/**
	 * Returns the raw data encoded in the barcode.
	 * 
	 * @return the raw barcode text string
	 */
	public String getBarcodeStringRaw() {

		return strRaw;
	}

	/**
	 * Returns a _nice_ string representing the data encoded in the barcode.
	 * 
	 * @return a _nice_ barcode text string
	 */
	public String getBarcodeStringNice() {

		return strNice;
	}

	/**
	 * Returns an encoded string representing the data encoded in the barcode.
	 * 
	 * @return an encoded barcode text string
	 */
	public String getBarcodeStringEncoded() {

		return strEncoded;
	}

	/**
	 * returns the number of bytes in the data array.
	 * 
	 * @return number of data bytesS
	 */
	public int getBarcodeDataSize() {

		return data.length;
	}

	/**
	 * Returns the barcode data encoded as Base64.
	 * 
	 * @return barcode data in Base64 format
	 */
	public String encodeBase64() {

		return Base64.getEncoder()//
				.encodeToString(getBarcodeData());
	}

	/**
	 * Returns the barcode data encoded as Base64, wrapped in a JSON object.
	 * 
	 * @return barcode data in JSON format
	 */
	public String encodeJSON() {

		return (new JSONObject()//
				.put("type", getBarcodeType())//
				.put("encoded", getBarcodeStringEncoded())//
				.put("base64", encodeBase64())//
		).toString();
	}
}
