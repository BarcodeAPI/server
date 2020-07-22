package org.barcodeapi.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;

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
}
