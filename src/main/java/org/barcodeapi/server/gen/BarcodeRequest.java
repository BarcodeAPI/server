package org.barcodeapi.server.gen;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * BarcodeRequest.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeRequest {

	private final CodeType type;

	private final String data;
	private final boolean complex;
	private final JSONObject options;

	private final boolean cached;
	private final int cost;

	private BarcodeRequest(CodeType type, String data, JSONObject options) {
		LibMetrics.hitMethodRunCounter();

		this.type = type;
		this.data = data;
		this.options = options;

		// Use cache based on options and data length
		this.complex = ((options.length() > 0) || (data.length() >= 48));
		this.cached = (type.getCacheEnable() && (!complex));

		// Calculate the cost of the request
		boolean free = (data.equals(type.getExample()));
		int cost = (complex) ? type.getCostCustom() : type.getCostBasic();
		this.cost = (free) ? 0 : cost;
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
	 * Returns the token cost for the request.
	 * 
	 * @return the barcode token cost
	 */
	public int getCost() {
		return cost;
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

		String opts = "";
		for (String key : getOptions().keySet()) {
			opts += (key + '=' + getOptions().getString(key) + '&');
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
		if (type == null || target == null || target.equals("")) {

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

		// Match against blacklist entries
		JSONArray blacklist = AppConfig.get().getJSONArray("blacklist");
		for (int x = 0; x < blacklist.length(); x++) {

			// Fail if request matches blacklist entry
			if (target.matches(blacklist.getString(x))) {
				throw new GenerationException(ExceptionType.BLACKLIST, //
						new Throwable("The request was rejected."));
			}
		}

		// Parse control characters
		if (type.getAllowNonprinting()) {
			target = CodeUtils.parseControlChars(target);
		}

		// Get and parse options
		JSONObject opts = CodeUtils.parseOptions(type, options);

		// Add back unparsed options
		if (options.length() > 0 && opts.length() == 0) {
			target += ("?" + options);
		}

		// Return the BarcodeRequest object
		return new BarcodeRequest(type, target, opts);
	}
}
