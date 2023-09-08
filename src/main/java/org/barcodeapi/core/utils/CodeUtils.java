package org.barcodeapi.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

public class CodeUtils {

	/**
	 * Converts a data string into a string containing control characters; Any
	 * instance of [$$?] will be converted into it's control character equivalent,
	 * offset by 64 from the given character.
	 * 
	 * A$$@A --> A(NUL)A
	 * 
	 * A$$_A --> A(US)A
	 * 
	 * @param offset
	 * @param data
	 * @return
	 */
	public static String parseControlChars(String data) {
		LibMetrics.hitMethodRunCounter();

		String newData = "";

		for (int x = 0; x < data.length(); x++) {

			if (data.length() > x + 2 && data.charAt(x) == '$' && data.charAt(x + 1) == '$') {

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
	 * @param data
	 * @param count
	 * @return
	 */
	public static int calculateEanChecksum(String data, int count) {
		LibMetrics.hitMethodRunCounter();

		int sum0 = 0;
		int sum1 = 0;

		for (int x = count; x > 1; x--) {

			int digit = Character.getNumericValue(data.charAt(count - x));

			if (x % 2 == 0) {

				sum1 += (3 * digit);
			} else {

				sum0 += digit;
			}
		}

		int sum = sum0 + sum1;

		int check = 10 - (sum % 10);

		if (check == 10) {

			return 0;
		} else {

			return check;
		}
	}

	/**
	 * Encode a UTF-8 string to a URL.
	 * 
	 * @param data
	 * @return
	 */
	public static String encode(String data) {
		LibMetrics.hitMethodRunCounter();

		try {

			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * Decode a URL to a UTF-8 string.
	 * 
	 * @param data
	 * @return
	 */
	public static String decode(String data) {
		LibMetrics.hitMethodRunCounter();

		try {

			return URLDecoder.decode(data, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Strip illegal characters for building a nice file name.
	 * 
	 * @param data
	 * @return
	 */
	public static String stripIllegal(String data) {
		LibMetrics.hitMethodRunCounter();

		return data.replaceAll("[!@#$%^&*\\(\\)\\[\\]\\{\\};:\\',\\<\\>\\\"]", "");
	}

	/**
	 * Returns a JSON map of options from the passed query string.
	 * 
	 * @param opts
	 * @return
	 */
	public static JSONObject parseOptions(String opts) {
		LibMetrics.hitMethodRunCounter();

		JSONObject options = new JSONObject();

		String[] parts = opts.split("&");

		for (String option : parts) {

			String[] kv = option.split("=");
			options.put(kv[0], kv[1]);
		}

		return options;
	}
}
