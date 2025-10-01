package org.barcodeapi.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.barcodeapi.core.Config;
import org.barcodeapi.core.Config.Cfg;
import org.barcodeapi.server.core.CodeType;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * CodeUtils.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CodeUtils {

	private static final String[] blacklist;

	static {
		JSONArray asJSON = Config.get(Cfg.Blacklist).getJSONArray("blacklist");
		blacklist = new String[asJSON.length()];
		for (int x = 0; x < asJSON.length(); x++) {
			blacklist[x] = asJSON.getString(x);
		}
	}

	public static boolean isBlacklisted(String data) {

		// Loop each blacklist item
		for (String entry : blacklist) {

			// Check if string is blacklisted
			if (data.matches(entry)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Converts a data string into a string containing control characters; Any
	 * instance of [$$?] will be converted into it's control character equivalent,
	 * offset by 64 from the given character.
	 * 
	 * A$$@A --> A(NUL)A
	 * 
	 * A$$_A --> A(US)A
	 * 
	 * @param data the data to parse
	 * @return the parsed string
	 */
	public static String parseControlChars(String data) {
		LibMetrics.hitMethodRunCounter();

		String newData = "";

		for (int x = 0; x < data.length(); x++) {

			if ((data.length() > (x + 2)) && //
					(data.charAt(x) == '$') && //
					(data.charAt(x + 1) == '$')) {

				newData += (char) (((int) data.charAt(x += 2)) - 64);
			} else {

				newData += data.charAt(x);
			}
		}

		return newData;
	}

	/**
	 * Calculate the checksum for an EAN type barcode.
	 * 
	 * @param data  the EAN data
	 * @param count the number of digits
	 * @return the checksum value
	 */
	public static int calculateChecksum(int checkDigit, String data) {
		LibMetrics.hitMethodRunCounter();

		int sum0, sum1 = sum0 = 0;
		for (int x = checkDigit; x > 1; x--) {

			int digit = Character.getNumericValue(data.charAt(checkDigit - x));

			if (x % 2 == 0) {
				sum1 += (3 * digit);
			} else {
				sum0 += digit;
			}
		}

		int check = (10 - ((sum0 + sum1) % 10));

		return (check == 10) ? 0 : check;
	}

	/**
	 * Encode a UTF-8 string to a URL.
	 * 
	 * @param data the data to encode
	 * @return the encoded string
	 */
	public static String encodeURL(String data) {
		LibMetrics.hitMethodRunCounter();

		try {

			return URLEncoder.encode(data, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			throw new IllegalArgumentException(e);

		} catch (IllegalArgumentException e) {

			throw e;
		}
	}

	/**
	 * Decode a URL to a UTF-8 string.
	 * 
	 * @param data the data to decode
	 * @return the decoded string
	 */
	public static String decodeURL(String data) {
		LibMetrics.hitMethodRunCounter();

		try {

			return URLDecoder.decode(data, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			throw new IllegalArgumentException(e);

		} catch (IllegalArgumentException e) {

			throw e;
		}
	}

	/**
	 * Strip illegal characters for building a nice file name.
	 * 
	 * @param data the data to strip
	 * @return the stripped string
	 */
	public static String stripIllegal(String data) {
		LibMetrics.hitMethodRunCounter();

		return data.replaceAll("[!@#$%^&*\\(\\)\\[\\]\\{\\};:\\',\\<\\>\\\"]", "");
	}

	/**
	 * Returns a JSON map of options from the passed query string.
	 * 
	 * @param opts the options string
	 * @return the parsed options
	 */
	public static JSONObject parseOptions(CodeType type, String opts) {
		LibMetrics.hitMethodRunCounter();

		// No options if blank
		if (opts.isBlank()) {
			return (new JSONObject());
		}

		// Loop each supplied option
		JSONObject options = new JSONObject();
		for (String option : opts.split("&")) {

			// Split on [key=value]
			String[] kv = option.split("=");

			// Key is first part
			String _key = kv[0].toLowerCase();

			// Value is second (optional) part
			String _val = (kv.length == 2) ? //
					kv[1].toLowerCase() : "true";

			// Check if supported by type
			if (type.getOptions().has(_key)) {

				// Skip if default
				if (String.valueOf(//
						type.getDefaults().get(_key))//
						.equals(_val)) {
					continue;
				}

				// Add to render option map
				options.put(_key, _val);
			}
		}

		return options;
	}
}
