package org.barcodeapi.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import org.json.JSONObject;

public class StringUtils {

	public static String encode(String data) {

		try {

			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception e) {

			return null;
		}
	}

	public static String decode(String data) {

		try {

			return URLDecoder.decode(data, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			throw new IllegalArgumentException(e);
		}
	}

	public static String stripIllegal(String data) {

		return data.replaceAll("[!@#$%^&*\\(\\)\\[\\]\\{\\};:\\',\\<\\>\\\"]", "");
	}

	public static JSONObject parseOptions(String opts) {

		JSONObject options = new JSONObject();

		String[] parts = opts.split("&");

		for (String option : parts) {

			String[] kv = option.split("=");
			options.put(kv[0], kv[1]);
		}

		return options;
	}

	public static String sumSHA256(byte[] in) {

		try {

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return StringUtils.bytesToHex(md.digest(in));
		} catch (Exception e) {

			return null;
		}
	}

	public static String bytesToHex(byte[] bytes) {

		if (bytes == null) {

			throw new IllegalArgumentException("supplied value cannot be null");
		}

		char[] hexArray = "0123456789ABCDEF".toCharArray();

		char[] hexChars = new char[bytes.length * 2];

		for (int j = 0; j < bytes.length; j++) {

			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
