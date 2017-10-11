package org.barcodeapi.server.gen;

public enum CodeType {

	/**
	 * Codabar type code;
	 */
	CODABAR(new String[] { "codabar" }, //
			"[0-9-:$\\/.+]+", //
			"[0-9-:$\\/.+]+"), //

	/**
	 * EAN-8 type UPC code;
	 * 
	 * 7 numerical digits followed by a single checksum digit.
	 */
	EAN8(new String[] { "8", "ean8" }, //
			"[0-9]{7,8}", //
			"[0-9]{7,8}"),

	/**
	 * EAN-13 type UPC code;
	 * 
	 * 12 numerical digits followed by a single checksum digit.
	 */
	EAN13(new String[] { "13", "ean13" }, //
			"[0-9]{12,13}", //
			"[0-9]{12,13}"),

	/**
	 * Code39 type code;
	 * 
	 * Variable length consisting of only numbers and upper-case characters.
	 */
	Code39(new String[] { "39", "code39" }, //
			"[A-Z*0-9 -$%./+]+", //
			"[A-Z*0-9 -$%./+]+"),

	/**
	 * Code128 type code;
	 * 
	 * Variable length consisting of numbers, letters, and symbols.
	 */
	Code128(new String[] { "128", "code128" }, //
			"[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+", //
			"[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+"),

	/**
	 * QR type code;
	 * 
	 * A high density data code with error correction.
	 */
	QRCode(new String[] { "qr", "qrcode" }, //
			".*", //
			".*"),

	/**
	 * Data Matrix type code;
	 * 
	 * A high density data code with error correction.
	 */
	DataMatrix(new String[] { "matrix", "datamatrix", "data" }, //
			"[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}", //
			"[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}");

	/**
	 * Local Variables
	 */
	private final String[] types;
	private final String pattern;
	private final String extended;

	/**
	 * Create a new CodeType with its pattern and list of associated IDs.
	 * 
	 * @param typeStrings
	 */
	CodeType(String[] typeStrings, String pattern, String extendedPattern) {

		this.types = typeStrings;
		this.pattern = pattern;
		this.extended = extendedPattern;
	}

	/**
	 * Get a list of all IDs associated with a CodeType.
	 * 
	 * @return
	 */
	public String[] getTypeStrings() {

		return types;
	}

	/**
	 * Get the regular expression that matches on auto-typing.
	 * 
	 * @return
	 */
	public String getPattern() {

		return pattern;
	}

	/**
	 * Get the regular expression that validates the data for the code type.
	 * 
	 * @return
	 */
	public String getExtendedPattern() {

		return extended;
	}

	/**
	 * Get a CodeType object by any of its associated string IDs.
	 * 
	 * Will return null if none are found.
	 * 
	 * @param codeType
	 * @return
	 */
	public static CodeType fromString(String codeType) {

		// Convert to lower case
		codeType = codeType.toLowerCase();

		// Loop all known types
		for (CodeType type : CodeType.values()) {

			// Loop each defined type string
			for (String typeString : type.getTypeStrings()) {

				// Return on match
				if (codeType.equals(typeString)) {

					return type;
				}
			}
		}

		// Return no matches
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
		if (data.matches(EAN8.getPattern())) {

			return EAN8;
		}

		// Match EAN-13 format
		if (data.matches(EAN13.getPattern())) {

			return EAN13;
		}

		// Match Code39 format
		if (data.matches(Code39.getPattern())) {

			return Code39;
		}

		// Match Code128 format
		if (data.matches(Code128.getPattern())) {

			return Code128;
		}

		// Match QR format
		if (data.matches(QRCode.getPattern())) {

			return QRCode;
		}

		// Match DataMatrix format
		if (data.matches(DataMatrix.getPattern())) {

			return DataMatrix;
		}

		// Return null on no matches
		return null;
	}
}
