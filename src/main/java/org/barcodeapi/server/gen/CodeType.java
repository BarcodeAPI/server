package org.barcodeapi.server.gen;

public enum CodeType {

	/**
	 * EAN-8 type UPC code; 7 numerical digits followed by a single checksum digit.
	 */
	EAN8("[0-9]{7,8}", //
			new String[] { "8", "ean8" }),

	/**
	 * EAN-13 type UPC code; 12 numerical digits followed by a single checksum
	 * digit.
	 */
	EAN13("[0-9]{12,13}", //
			new String[] { "13", "ean13" }),

	/**
	 * Code39 type code; variable length consisting of only numbers and upper-case
	 * characters.
	 */
	Code39("[0-9A-Z]{1,32}", //
			new String[] { "39", "code39" }),

	/**
	 * Code128 type code; variable length consisting of numbers, letters, and
	 * symbols.
	 */
	Code128("[0-9A-Za-z]{1,32}", //
			new String[] { "128", "code128" }),

	/**
	 * QR type code; a high density data code with error correction.
	 */
	QRCode(".*", //
			new String[] { "qr", "qrcode" }),

	/**
	 * Data Matrix type code; a high density data code with error correction.
	 */
	DataMatrix(".*", //
			new String[] { "matrix", "datamatrix", "data" });

	private final String pattern;
	private final String[] typeStrings;

	/**
	 * Create a new CodeType with a list of its associated IDs.
	 * 
	 * @param typeStrings
	 */
	CodeType(String pattern, String[] typeStrings) {

		this.pattern = pattern;
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
	 * Returns true if the pattern passes validation for the code type.
	 * 
	 * @param data
	 * @return
	 */
	public boolean validateFormat(String data) {

		return data.matches(pattern);
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
		if (EAN8.validateFormat(data)) {

			return EAN8;
		}

		// Match EAN-13 format
		if (EAN13.validateFormat(data)) {

			return EAN13;
		}

		// Match Code39 format
		if (Code39.validateFormat(data) //
				&& data.length() < 16) {

			return Code39;
		}

		// Match Code128 format
		if (Code128.validateFormat(data) //
				&& data.length() < 24) {

			return Code128;
		}

		// Match QR format
		if (QRCode.validateFormat(data) //
				&& data.length() < 64) {

			return QRCode;
		}

		// Default to Data Matrix format
		return DataMatrix;
	}
}
