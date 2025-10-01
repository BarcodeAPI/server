package org.barcodeapi.core.utils;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

/**
 * AuthUtils.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class AuthUtils {

	public static void main(String[] args) {

		System.out.println(//
				formatUser(args[0], passHash(args[1])));
	}

	public static String passHash(String pass) {
		return LibExtrasHashes.sumSHA256(pass.getBytes());
	}

	public static String formatUser(String user, String hash) {
		return String.format("%s:%s", user, hash);
	}
}
