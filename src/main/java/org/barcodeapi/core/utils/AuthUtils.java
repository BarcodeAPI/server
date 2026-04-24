package org.barcodeapi.core.utils;

import com.mclarkdev.tools.libextras.LibExtrasHashes;
import com.mclarkdev.tools.libextras.LibExtrasHashes.HashType;

/**
 * AuthUtils.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class AuthUtils {

	public static void main(String[] args) {

		System.out.println(//
				formatUser(args[0], passHash(args[1])));
	}

	public static String passHash(String pass) {
		return LibExtrasHashes.checksum(//
				HashType.SHA256, pass.getBytes());
	}

	public static String formatUser(String user, String hash) {
		return String.format("%s:%s", user, hash);
	}
}
