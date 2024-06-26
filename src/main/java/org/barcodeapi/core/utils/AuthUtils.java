package org.barcodeapi.core.utils;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

/**
 * AuthUtils.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class AuthUtils {

	public static void main(String[] args) {

		String passHash = LibExtrasHashes.sumSHA256(args[1].getBytes());
		String userAuth = String.format("%s:%s", args[0], passHash);

		System.out.println(userAuth);
	}
}
