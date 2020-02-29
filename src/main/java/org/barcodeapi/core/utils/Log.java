package org.barcodeapi.core.utils;

public class Log {

	public static void i(String message) {
		log(true, false, message);
	}

	public static void w(String message) {
		log(true, true, message);
	}

	public static void e(String message, Throwable e) {
		log(false, true, message);
	}

	private static void log(boolean out, boolean err, String message) {

		if (out) {
			System.out.println(message);
		}

		if (err) {
			System.err.println(message);
		}
	}
}
