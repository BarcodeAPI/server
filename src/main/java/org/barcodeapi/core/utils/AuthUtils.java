package org.barcodeapi.core.utils;

public class AuthUtils {

	public static void main(String[] args) {

		String passHash = StringUtils.sumSHA256(args[1].getBytes());
		String userAuth = String.format("%s:%s", args[0], passHash);

		System.out.println(userAuth);
	}
}
