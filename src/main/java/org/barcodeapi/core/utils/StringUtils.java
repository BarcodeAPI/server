package org.barcodeapi.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.json.JSONObject;

public class StringUtils {

	public static String encode(String data) {

		try {

			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception e) {

			return null;
		}
	}

	public static String decode(String data) throws GenerationException {

		try {

			return URLDecoder.decode(data, "UTF-8");

		} catch (UnsupportedEncodingException | IllegalArgumentException e) {

			throw new GenerationException(ExceptionType.INVALID, e);
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
}
