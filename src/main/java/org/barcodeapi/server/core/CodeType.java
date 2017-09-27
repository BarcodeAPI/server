package org.barcodeapi.server.core;

public enum CodeType {

	/**
	 * 
	 */
	Code128(new String[] { "128" }),

	/**
	 * 
	 */
	QRCode(new String[] { "qr" }),

	/**
	 * 
	 */
	DataMatrix(new String[] { "matrix" });

	private String[] typeStrings;

	CodeType(String[] typeStrings) {

		this.typeStrings = typeStrings;
	}

	public String[] getTypeStrings() {

		return typeStrings;
	}

	public static CodeType fromString(String codeType) {

		for (CodeType type : CodeType.values()) {

			for (String typeString : type.getTypeStrings()) {

				if (codeType.equals(typeString)) {

					return type;
				}
			}
		}

		return null;
	}

	public static CodeType getType(String data) {

		// Longer then 256 bytes is DataMatrix
		if (data.length() > 256) {

			return CodeType.DataMatrix;
		}

		// Match URLs to QR
		if (data.matches("^(http://|https://).*")) {

			return CodeType.QRCode;
		}

		// Match Amazon ASIN to Code128
		if (data.matches("B[0-9a-zA-Z]{9}")) {

			return CodeType.Code128;
		}

		// Default to QR
		return CodeType.QRCode;
	}
}
