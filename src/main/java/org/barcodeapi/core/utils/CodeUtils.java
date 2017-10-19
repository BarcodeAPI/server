package org.barcodeapi.core.utils;

import java.security.MessageDigest;

public class CodeUtils {

	public static String getMD5Sum(byte[] image) {

		try {

			byte[] hash = MessageDigest.getInstance("MD5").digest(image);

			StringBuilder hexString = new StringBuilder();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xFF & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception e) {

			return null;
		}
	}

	// FIXME does not work correctly
	public static int calculateEanChecksum(String data) {

		int sum1 = 0;
		int sum2 = 0;
		for (int x = 0; x < data.length() - 1; x++) {

			int digit = Character.getNumericValue(data.charAt(x));

			if (x % 2 == 0) {

				sum2 += digit;
			} else {

				sum1 += digit;
			}
		}

		int sum = sum1 + (sum2 * 3);

		int check = (10 - (sum % 10));

		if (check == 10) {

			return 0;
		} else {

			return check;
		}
	}
}
