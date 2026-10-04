package org.barcodeapi.server.gen;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * BarcodeRequest.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026, Community Edition)
 */
public class BarcodeRequest {

	private final CodeType type;

	private final String data;
	private final JSONObject options;

	private final boolean complex;
	private final boolean cached;
	private final boolean download;
	private final boolean url;

	protected BarcodeRequest(//
			CodeType type, String data, JSONObject options) {
		LibMetrics.hitMethodRunCounter();

		this.type = type;
		this.data = data;

		// Use cache based on options and data length
		this.options = options;
		this.complex = ((options.length() > 0) || (data.length() >= 48));
		this.cached = (type.getCacheEnable() && (!complex));

		// User requested download
		this.download = options.optBoolean("download", false);

		// Determine if request is a URL
		boolean isUrl = false;
		if (data.regionMatches(true, 0, "https://", 0, 8) || //
				data.regionMatches(true, 0, "http://", 0, 7)) {
			isUrl = true;
		}

		// Request properties
		this.url = isUrl;
	}

	/**
	 * Returns the requested barcode type.
	 * 
	 * @return the requested barcode type
	 */
	public CodeType getType() {
		return type;
	}

	/**
	 * Returns the requested data content.
	 * 
	 * @return the requested data content
	 */
	public String getData() {
		return data;
	}

	/**
	 * Returns true if the barcode is complex.
	 * 
	 * @return the barcode is complex
	 */
	public boolean isComplex() {
		return complex;
	}

	/**
	 * Returns true if the request should use the cache.
	 * 
	 * @return true if use cache
	 */
	public boolean useCache() {
		return cached;
	}

	/**
	 * Returns true if server should force a download.
	 * 
	 * @return true if force downloading
	 */
	public boolean forceDownload() {
		return download;
	}

	/**
	 * Returns true if the request is a URL.
	 * 
	 * @return true if request is a URL
	 */
	public boolean isURL() {
		return url;
	}

	/**
	 * Returns a map of options used to generate the barcode.
	 * 
	 * @return a map of request options
	 */
	public JSONObject getOptions() {
		return options;
	}

	/**
	 * Returns the request as a URI.
	 * 
	 * @return the request as a URI
	 */
	public String encodeURI() {
		LibMetrics.hitMethodRunCounter();

		String opts = "";
		for (String key : getOptions().keySet()) {
			opts += String.format("%s=%s&", key, getOptions().getString(key));
		}

		return String.format("/api/%s/%s?%s", //
				getType().getTargets()[0], getData(), opts);
	}

	/**
	 * Returns a BarcodeRequest object for the given CSV entry.
	 * 
	 * @param record the CSV record
	 * @return the barcode request object
	 * @throws GenerationException processing failure
	 */
	public static BarcodeRequest fromCSV(String[] record) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		if (record == null || record.length == 0) {
			throw new GenerationException(ExceptionType.EMPTY, //
					new Throwable("The request was empty."));
		}

		String data = record[0];
		String type = (record.length > 1) ? record[1] : "auto";

		String args = "";
		for (int x = 2; x < record.length; x++) {
			if ((record[x] == null) || //
					(record[x].length() == 0)) {
				continue;
			}
			args += (record[x] + '&');
		}

		return fromURI(String//
				.format("/api/%s/%s?%s", type, data, args));
	}

	/**
	 * Returns a BarcodeRequest object for the given URI.
	 * 
	 * @param uri the resource identifier
	 * @return the barcode request object
	 * @throws GenerationException processing failure
	 */
	public static BarcodeRequest fromURI(String uri) throws GenerationException {
		LibMetrics.hitMethodRunCounter();

		// Split target from options
		int index = uri.indexOf('?');
		String target = (index == -1) ? uri : uri.substring(0, index);
		String options = (index == -1) ? "" : uri.substring(index + 1);

		// Remove leading [/api/] or [/]
		target = target.substring(//
				target.startsWith("/api/") ? 5 : 1);

		try {

			// Get raw string from URL encoding
			target = CodeUtils.decodeURL(target);
		} catch (IllegalArgumentException e) {

			// Log and throw the decoding failure
			throw LibLog._clog("E0608").asException();
		}

		// Extract code type and data string
		CodeType type;
		int typeIndex = target.indexOf("/");
		if (typeIndex > 0) {

			// Get the type string
			String typeString = target.substring(0, typeIndex);

			// Type is auto
			if (typeString.equals("auto")) {

				// No type specified
				target = target.substring(5);
				type = TypeSelector.getTypeFromData(target);

			} else {

				// Check if generator found for given type
				type = TypeSelector.getTypeFromString(typeString);
				if (type == null) {

					// No type specified
					type = TypeSelector.getTypeFromData(target);
				} else {

					// Set data string to omit type
					target = target.substring(typeIndex + 1);
				}
			}
		} else {

			// No type specified
			type = TypeSelector.getTypeFromData(target);
		}

		// Check for valid render type and data
		if (type == null || target == null || target.isEmpty()) {

			// Fail on empty requests
			throw new GenerationException(ExceptionType.EMPTY, //
					new Throwable("The request was empty."));
		}

		// Validate barcode pattern
		if (!target.matches(type.getPatternExtended())) {

			// Fail if request does not match pattern
			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Invalid data for selected code type."));
		}

		// Check if the request is a URL
		if (target.regionMatches(true, 0, "http", 0, 4)) {

			// Check position where the second '/' should be
			int slashPos = target.regionMatches(//
					true, 4, "s:/", 0, 3) ? 7 : 6;

			// Check for broken URLs
			if ((target.length() > slashPos) && //
					(target.charAt(slashPos - 1) == '/') && //
					(target.charAt(slashPos) != '/')) {

				// Fix the broken URL
				target = target.substring(0, slashPos) + //
						'/' + target.substring(slashPos);
			}
		}

		// Check barcode type supports and contains control chars
		if (type.getAllowNonprinting() && target.contains("$$")) {

			// Parse control characters
			target = CodeUtils.parseControlChars(target);
		}

		// Parse checksum character
		if (type.enforceChecksum()) {

			// Calculate the expected checksum value for the data
			int expected = CodeUtils.calculateChecksum(type.getCheckDigit(), target);

			// Check if digit needs to be added
			if (target.length() < type.getCheckDigit()) {

				LibLog._logF("Request is missing check digit, adding %d", expected);
				target = (target + expected);

			} else if (target.length() >= type.getCheckDigit()) {

				// Check if check digit is as expected
				if ((target.charAt(type.getCheckDigit() - 1) - '0') != expected) {

					// Fail if actual check digit is different then expected
					throw new GenerationException(ExceptionType.CHECKSUM, //
							new Throwable(String.format("Invalid checksum: expected %d", expected)));
				}
			}
		}

		// Get and parse request options
		JSONObject opts = CodeUtils.parseOptions(type, options);

		// Add back unparsed options
		if (options.length() > 0 && opts.length() == 0) {
			target += ("?" + options);
		}

		// Return the assembled BarcodeRequest object
		return new BarcodeRequest(type, target, opts);
	}
}
