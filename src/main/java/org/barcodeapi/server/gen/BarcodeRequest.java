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

	private final CodeType type;
	private final String data;
	private final int cost;
	private final boolean cached;
	private final JSONObject options;

	public BarcodeRequest(String target) {
		LibMetrics.hitMethodRunCounter();

		// remove [ /api ]
		if (target.startsWith("/api")) {
			target = target.substring(4);
		}

		// get and decode the request string
		String[] parts = target.split("\\?");
		String data = CodeUtils.decodeURL(parts[0].substring(1));

		// extract code type and data string
		CodeType type;
		int typeIndex = data.indexOf("/");
		if (typeIndex > 0) {

			// get the type string
			String typeString = data.substring(0, typeIndex);

			// type is auto
			if (typeString.equals("auto")) {

				// no type specified
				data = data.substring(5);
				type = TypeSelector.getTypeFromData(data);

			} else {

				// check if generator found for given type
				type = TypeSelector.getTypeFromString(typeString);
				if (type == null) {

					// no type specified
					type = TypeSelector.getTypeFromData(data);
				} else {

					// set data string to omit type
					data = data.substring(typeIndex + 1);
				}
			}
		} else {

			// no type specified
			type = TypeSelector.getTypeFromData(data);
		}

		// get and parse options
		this.options = ((parts.length == 2) ? //
				CodeUtils.parseOptions(parts[1]) : new JSONObject());

		// use cache based on options and data length
		this.cached = ((options.length() == 0) && (data.length() <= 48));

		// Calculate the cost of the request
		this.cost = (cached) ? type.getCostBasic() : type.getCostCustom();

		this.type = type;
		this.data = data;
	}

	/**
	 * Returns the requested barcode type.
	 * 
	 * @return
	 */
	public CodeType getType() {
		return type;
	}

	/**
	 * Returns the requested data content.
	 * 
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * Returns the token cost for the request.
	 * 
	 * @return
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * Returns true if the request should use the cache.
	 * 
	 * @return
	 */
	public boolean useCache() {
		return cached;
	}

	/**
	 * Returns a map of options used to generate the barcode.
	 * 
	 * @return
	 */
	public JSONObject getOptions() {
		return options;
	}
}
