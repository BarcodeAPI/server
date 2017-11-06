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

	public static String parseControlChars(int offset, String data) {

		String newData = "";

		for (int x = 0; x < data.length(); x++) {

			if (data.charAt(x) == '$' && data.charAt(x + 1) == '$') {

				newData += (char) (((int) data.charAt(x += 2)) - offset);
			} else {

				newData += data.charAt(x);
			}
		}

		return newData;
	}

	// FIXME does not work correctly
	public static int calculateEanChecksum(String data, int count) {

		int sum0 = 0;
		int sum1 = 0;

		for (int x = count; x > 1; x--) {

			int digit = Character.getNumericValue(data.charAt(count - x));

			if (x % 2 == 0) {

				sum1 += (3 * digit);
			} else {

				sum0 += digit;
			}
		}

		int sum = sum0 + sum1;

		int check = 10 - (sum % 10);

		if (check == 10) {

			return 0;
		} else {

			return check;
		}
	}
}
