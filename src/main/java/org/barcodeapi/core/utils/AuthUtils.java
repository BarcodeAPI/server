package org.barcodeapi.core.utils;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

public class AuthUtils {

	public static void main(String[] args) {

		String passHash = LibExtrasHashes.sumSHA256(args[1].getBytes());
		String userAuth = String.format("%s:%s", args[0], passHash);

		System.out.println(userAuth);
	}
}
