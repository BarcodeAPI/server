package org.barcodeapi.server.gen;

public enum CodeType {

	/**
	 * EAN-8 type UPC code; 7 numerical digits followed by a single checksum digit.
	 */
	EAN8(new String[] { "8", "ean8" }),

	/**
	 * EAN-13 type UPC code; 12 numerical digits followed by a single checksum
	 * digit.
	 */
	EAN13(new String[] { "13", "ean13" }),

	/**
	 * Code39 type code; variable length consisting of only numbers and upper-case
	 * characters.
	 */
	Code39(new String[] { "39", "code39" }),

	/**
	 * Code128 type code; variable length consisting of numbers, letters, and
	 * symbols.
	 */
	Code128(new String[] { "128", "code128" }),

	/**
	 * QR type code; a high density data code with error correction.
	 */
	QRCode(new String[] { "qr", "qrcode" }),

	/**
	 * Data Matrix type code; a high density data code with error correction.
	 */
	DataMatrix(new String[] { "matrix", "datamatrix", "data" });

	private String[] typeStrings;

	/**
	 * Create a new CodeType with a list of its associated IDs.
	 * 
	 * @param typeStrings
	 */
	CodeType(String[] typeStrings) {

		this.typeStrings = typeStrings;
	}

	/**
	 * Get a list of all IDs associated with a CodeType.
	 * 
	 * @return
	 */
	public String[] getTypeStrings() {

		return typeStrings;
	}

	/**
	 * Get a CodeType object by any of its associated string IDs. Will return null
	 * if none are found.
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
	 * Returns a CodeType object best suited for the given data string.
	 * 
	 * @param data
	 * @return
	 */
	public static CodeType getType(String data) {

		// Match EAN-8 format
		if (data.matches("[0-9]{7,8}")) {

			return CodeType.EAN8;
		}

		// Match EAN-13 format
		if (data.matches("[0-9]{12,13}")) {

			return CodeType.EAN13;
		}

		// Match letters and numbers to Code39
		if (data.matches("[A-Z0-9]{1,12}")) {

			return CodeType.Code39;
		}

		// Match URLs to QR
		if (data.matches("^(http://|https://).*")) {

			return CodeType.QRCode;
		}

		// Less then 16 bytes is Code128
		if (data.length() < 16) {

			return CodeType.Code128;
		}

		// Longer then 64 bytes is DataMatrix
		if (data.length() < 32) {

			return CodeType.QRCode;
		}

		// Default to DataMatrix
		return CodeType.DataMatrix;
	}
}
