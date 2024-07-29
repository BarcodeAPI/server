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

	private CodeType type;
	private String data;
	private boolean cached;
	private JSONObject options;

	private BarcodeRequest() {
	}

	public static BarcodeRequest fromURI(String target) {
		LibMetrics.hitMethodRunCounter();

		// remove [ /api ]
		if (target.startsWith("/api")) {
			target = target.substring(4);
		}

		// get and decode the request string
		String[] parts = target.split("\\?");
		String data = CodeUtils.decode(parts[0].substring(1));

		// get and parse options
		JSONObject options = new JSONObject();
		if (parts.length == 2) {
			options = CodeUtils.parseOptions(parts[1]);
		}

		// use cache based on options and data length
		boolean cached = ((options.length() == 0) && (data.length() <= 48));

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

		// create and return request object
		BarcodeRequest r = new BarcodeRequest();
		r.type = type;
		r.data = data;
		r.cached = cached;
		r.options = options;
		return r;
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
