package org.barcodeapi.server.core;

public enum CodeType {

	/**
	 * 
	 */
	Code39(new String[] { "39", "code39" }),

	/**
	 * 
	 */
	Code128(new String[] { "128", "code128" }),

	/**
	 * 
	 */
	QRCode(new String[] { "qr", "qrcode" }),

	/**
	 * 
	 */
	DataMatrix(new String[] { "matrix", "datamatrix", "data" });

	private String[] typeStrings;

	/**
	 * 
	 * @param typeStrings
	 */
	CodeType(String[] typeStrings) {

		this.typeStrings = typeStrings;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getTypeStrings() {

		return typeStrings;
	}

	/**
	 * 
	 * @param codeType
	 * @return
	 */
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

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static CodeType getType(String data) {

		// Match numbers only to Code128
		if (data.matches("[0-9]{1,8}")) {

			return CodeType.Code39;
		}

		// Match numbers only to Code128
		if (data.matches("[0-9]{1,24}")) {

			return CodeType.Code128;
		}

		// Match URLs to QR
		if (data.matches("^(http://|https://).*")) {

			return CodeType.QRCode;
		}

		// Match Amazon ASIN to Code128
		if (data.matches("B[0-9a-zA-Z]{9}")) {

			return CodeType.Code128;
		}

		// Longer then 256 bytes is DataMatrix
		if (data.length() > 64) {

			return CodeType.DataMatrix;
		}

		// Default to QR
		return CodeType.QRCode;
	}
}
