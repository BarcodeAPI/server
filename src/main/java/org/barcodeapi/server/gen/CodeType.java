package org.barcodeapi.server.gen;

public enum CodeType {

	/**
	 * UPC-E type UPC code;
	 */
	UPC_E(new String[] { "e", "upc-e", "upce" }, //
			"^(?=.*0)[0-9]{8}$", //
			"^(?=.*0)[0-9]{7,8}$", //
			"01023459", //
			"A compact version of UPC-A which removes unneeded '0's."),

	/**
	 * UPC-A type UPC code;
	 */
	UPC_A(new String[] { "a", "upc-a", "upca", "upc" }, //
			"^(?=.*0)[0-9]{12}$", //
			"^(?=.*0)[0-9]{11,12}$", //
			"123456789012", //
			"Used as a product identifier in many countries, the 12 digit barcode allows information about manufacturer, product, variant, and additional store use codes."),

	/**
	 * EAN-8 type UPC code;
	 * 
	 * 7 numerical digits followed by a single checksum digit.
	 */
	EAN8(new String[] { "8", "ean-8", "ean8" }, //
			"^[0-9]{8}$", //
			"^[0-9]{7,8}$", //
			"01023459", //
			"A UPC type barcode, derived from the longer EAN-13 code, for use on smaller pakages."),

	/**
	 * EAN-13 type UPC code;
	 * 
	 * 12 numerical digits followed by a single checksum digit.
	 */
	EAN13(new String[] { "13", "ean-13", "ean13" }, //
			"^[0-9]{13}$", //
			"^[0-9]{12,13}$", //
			"1234567890128", //
			"A globally recognized product identification code."),

	/**
	 * Codabar type code;
	 */
	CODABAR(new String[] { "codabar" }, //
			"^[0-9:$]{4,12}$", //
			"^[0-9-:$\\/.+]+$", //
			"1234567890", //
			"An early barode designed to be printer on dot-matrix printers."),

	/**
	 * Code39 type code;
	 * 
	 * Variable length consisting of only numbers and upper-case characters.
	 */
	Code39(new String[] { "39", "code-39", "code39" }, //
			"^[A-Z0-9 $.\\/]{1,12}$", //
			"^[A-Z*0-9 -$%.\\/+]+$", //
			"TRY 39 ME", //
			"A basic alphanumeric barcode."),

	/**
	 * Code128 type code;
	 * 
	 * Variable length consisting of numbers, letters, and symbols.
	 */
	Code128(new String[] { "128", "code-128", "code128" }, //
			"^[ !#$()*.\\/0-9=?A-Z_a-z~]{1,16}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+$", //
			"Try Me!", //
			"A high desnisty alphanumeric barcode."),

	/**
	 * Aztec type barcode.
	 *
	 * A square 2d barcode resembling a pyramid.
	 */
	Aztec(new String[] { "aztec" }, //
			"^[ !#$()*.\\/0-9=?A-Z_a-z~]{1,16}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+$", //
			"Aztec Barcode", //
			"A 2D data barcode whose timing markers radiate outward from the middle."),

	/**
	 * QR type code;
	 * 
	 * A high density data code with error correction.
	 */
	QRCode(new String[] { "qr", "qr-code", "qrcode" }, //
			"^.{1,64}$", //
			"^.{1,65535}$", //
			"QR Barcode", //
			"A 2D data barcode whose timing markers are aligned at the corners."),

	/**
	 * Data Matrix type code;
	 * 
	 * A high density data code with error correction.
	 */
	DataMatrix(new String[] { "dm", "data-matrix", "datamatrix", "matrix", "data" }, //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"Data Matrix Barcode", //
			"A 2D data barode whose timing markers are along the the edges."),

	/**
	 * PDF417
	 * 
	 * 
	 */
	PDF417(new String[] { "417", "pdf417", "pdf" }, //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"PDF - 417", //
			"A stacked linear barcode most commonly found on identification cards.");

	/**
	 * Local Variables
	 */
	private final String[] types;

	private final String autoPattern;
	private final String formatPattern;

	private final String example;
	private final String description;

	/**
	 * Create a new CodeType with its pattern and list of associated IDs.
	 * 
	 * @param typeStrings
	 */
	CodeType(String[] typeStrings, String automatchPattern, //
			String extendedPattern, String example, String description) {

		this.types = typeStrings;

		this.autoPattern = automatchPattern;
		this.formatPattern = extendedPattern;

		this.example = example;
		this.description = description;
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
	public String getAutomatchPattern() {

		return autoPattern;
	}

	/**
	 * Get the regular expression that validates the data for the code type.
	 * 
	 * @return
	 */
	public String getFormatPattern() {

		return formatPattern;
	}

	/**
	 * Get an example barcode format.
	 * 
	 * @return
	 */
	public String getExample() {

		return example;
	}

	/**
	 * Get the description of the specific barcode type.
	 * 
	 * @return
	 */
	public String getDescription() {

		return description;
	}
}
