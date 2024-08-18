package org.barcodeapi.server.gen;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * BarcodeRequest.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class BarcodeRequest {

	private final String uri;

	private final CodeType type;
	private final String data;
	private final int cost;
	private final boolean cached;
	private final JSONObject options;

	public BarcodeRequest(String target) {
		LibMetrics.hitMethodRunCounter();

		// Remove [ /api ]
		if (target.startsWith("/api")) {
			target = target.substring(4);
		}

		// Assign URI
		this.uri = target;

		// Get and decode the request string
		String[] parts = target.split("\\?");
		String data = CodeUtils.decodeURL(parts[0].substring(1));

		// Extract code type and data string
		CodeType type;
		int typeIndex = data.indexOf("/");
		if (typeIndex > 0) {

			// Get the type string
			String typeString = data.substring(0, typeIndex);

			// Type is auto
			if (typeString.equals("auto")) {

				// No type specified
				data = data.substring(5);
				type = TypeSelector.getTypeFromData(data);

			} else {

				// Check if generator found for given type
				type = TypeSelector.getTypeFromString(typeString);
				if (type == null) {

					// No type specified
					type = TypeSelector.getTypeFromData(data);
				} else {

					// Set data string to omit type
					data = data.substring(typeIndex + 1);
				}
			}
		} else {

			// No type specified
			type = TypeSelector.getTypeFromData(data);
		}

		// Get and parse options
		this.options = ((parts.length == 2) ? //
				CodeUtils.parseOptions(parts[1]) : new JSONObject());

		// Use cache based on options and data length
		this.cached = ((options.length() == 0) && (data.length() <= 48));

		// Calculate the cost of the request
		this.cost = (cached) ? type.getCostBasic() : type.getCostCustom();

		// Final type & data
		this.type = type;
		this.data = data;
	}

	/**
	 * Returns the URI for the request.
	 * 
	 * @return the URI for the request
	 */
	public String getURI() {
		return uri;
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
}
