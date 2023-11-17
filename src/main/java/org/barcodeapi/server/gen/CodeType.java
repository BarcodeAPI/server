package org.barcodeapi.server.gen;

import org.json.JSONObject;

public enum CodeType {

	/**
	 * UPC-E type UPC code;
	 */
	UPC_E(1, new String[] { "e", "upc-e", "upce" }, //
			"^(?=.*0)[0-9]{8}$", //
			"^(?=.*0)[0-9]{7,8}$", //
			"01023459", //
			"A compact version of UPC-A, removing unneeded '0's."),

	/**
	 * UPC-A type UPC code;
	 */
	UPC_A(1, new String[] { "a", "upc-a", "upca", "upc" }, //
			"^(?=.*0)[0-9]{12}$", //
			"^(?=.*0)[0-9]{11,12}$", //
			"123456789012", //
			"A universally recognized 12 digit barcode. Encodes manufacturer, product / variant, and store use codes."),

	/**
	 * EAN-8 type UPC code;
	 * 
	 * 7 numerical digits followed by a single checksum digit.
	 */
	EAN8(1, new String[] { "8", "ean-8", "ean8" }, //
			"^[0-9]{8}$", //
			"^[0-9]{7,8}$", //
			"01023459", //
			"Derived from the longer EAN-13 UPC code. For smaller packages."),

	/**
	 * EAN-13 type UPC code;
	 * 
	 * 12 numerical digits followed by a single checksum digit.
	 */
	EAN13(1, new String[] { "13", "ean-13", "ean13" }, //
			"^[0-9]{13}$", //
			"^[0-9]{12,13}$", //
			"1234567890128", //
			"A globally recognized product identification code. Encodes manufacturer, product / variant, and store use codes."),

	/**
	 * Codabar type code;
	 */
	CODABAR(1, new String[] { "codabar" }, //
			"^[0-9:$]{4,12}$", //
			"^[0-9-:$\\/.+]+$", //
			"1234567890", //
			"An early barode designed to be printed on dot-matrix printers."),

	/**
	 * ITF-14 type code;
	 */
	ITF14(1, new String[] { "14", "itf-14", "scc-14", "gtin" }, //
			"^[0-9]{14}$", //
			"^[0-9]{14}$", //
			"98765432109213", //
			"Interleaved 2 of 5, type 14."),

	/**
	 * Code39 type code;
	 * 
	 * Variable length consisting of only numbers and upper-case characters.
	 */
	Code39(2, new String[] { "39", "code-39", "code39" }, //
			"^[A-Z0-9 $.\\/]{1,12}$", //
			"^[A-Z*0-9 -$%.\\/+]+$", //
			"TRY 39 ME", //
			"A basic alphanumeric barcode. Uppercase letters and numbers only."),

	/**
	 * Code128 type code;
	 * 
	 * Variable length consisting of numbers, letters, and symbols.
	 */
	Code128(2, new String[] { "128", "code-128", "code128" }, //
			"^[ !#$()*.\\/0-9=?A-Z_a-z~]{1,16}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+$", //
			"Try Me!", //
			"A medium densisty alphanumeric barcode with a larger character set."),

	/**
	 * Aztec type barcode.
	 *
	 * A square 2d barcode resembling a pyramid.
	 */
	Aztec(3, new String[] { "aztec" }, //
			"^[ !#$()*.\\/0-9=?A-Z_a-z~]{1,16}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]+$", //
			"Aztec Barcode", //
			"A 2D data barcode whose timing markers radiate outward from the middle."),

	/**
	 * QR type code;
	 * 
	 * A high density data code with error correction.
	 */
	QRCode(3, new String[] { "qr", "qr-code", "qrcode" }, //
			"^.{1,64}$", //
			"^.{1,65535}$", //
			"QR Barcode", //
			"A 2D data barcode whose timing markers are aligned at the corners."),

	/**
	 * Data Matrix type code;
	 * 
	 * A high density data code with error correction.
	 */
	DataMatrix(3, new String[] { "dm", "data-matrix", "datamatrix", "matrix", "data" }, //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"Data Matrix Barcode", //
			"A 2D data barode whose timing markers are along the the edges."),

	/**
	 * PDF417
	 * 
	 * 
	 */
	PDF417(3, new String[] { "417", "pdf417", "pdf" }, //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"^[ !\"#$%&'()*+,-.\\/0-9:;<=>?@A-Z\\[\\\\\\]^_`a-z{|}~]{1,2335}$", //
			"PDF - 417", //
			"A stacked linear barcode most commonly found on identification cards."),

	/**
	 * USPS Intelligent Mail
	 */
	USPSMail(2, new String[] { "usps", "intelligent-mail" }, //
			"^[0-9]{1,32}$", //
			"^[0-9]{1,32}$", //
			"0123456709498765432101234567891", //
			"USPS Intelligent Mail"),

	/**
	 * Royal Mail
	 */
	RoyalMail(2, new String[] { "royal", "royal-mail" }, //
			"^[0-9]{1,32}$", //
			"^[0-9]{1,32}$", //
			"11212345612345678", //
			"Royal Mail");

	/**
	 * Local Variables
	 */
	private final int cost;
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
	CodeType(int cost, String[] typeStrings, String automatchPattern, //
			String extendedPattern, String example, String description) {

		this.cost = cost;

		this.types = typeStrings;

		this.autoPattern = automatchPattern;
		this.formatPattern = extendedPattern;

		this.example = example;
		this.description = description;
	}

	/**
	 * Returns the token cost to render the barcode.
	 */
	public int getBaseCost() {

		return cost;
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

	/**
	 * Return the the code type as a JSON object.
	 * 
	 * @return
	 */
	public JSONObject toJSON() {

		return new JSONObject() //
				.put("name", name())//
				.put("example", getExample())//
				.put("target", getTypeStrings()[0])//
				.put("pattern", getFormatPattern())//
				.put("description", getDescription());
	}
}
