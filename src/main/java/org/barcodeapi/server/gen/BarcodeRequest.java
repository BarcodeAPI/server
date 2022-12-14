package org.barcodeapi.server.gen;

import org.barcodeapi.core.utils.StringUtils;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONObject;

public class BarcodeRequest {

	private final CodeType type;
	private final String data;
	private final boolean cached;
	private final JSONObject options;

	public BarcodeRequest(String target) {

		// remove [ /api ]
		if (target.startsWith("/api")) {
			target = target.substring(4);
		}

		// get and decode the request string
		String[] parts = target.split("\\?");
		String data = StringUtils.decode(parts[0].substring(1));

		// get and parse options
		JSONObject options = new JSONObject();
		if (parts.length == 2) {
			options = StringUtils.parseOptions(parts[1]);
		}

		// use cache based on options
		boolean cached = true;
		if (options.length() > 0) {
			cached = false;
		}
		if (data.length() > 64) {
			cached = false;
		}
		if (options.optBoolean("no-cache", false)) {
			cached = false;
		}

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

		// assign class variables
		this.type = type;
		this.data = data;
		this.cached = cached;
		this.options = options;
	}

	public CodeType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public boolean useCache() {
		return cached;
	}

	public JSONObject getOptions() {
		return options;
	}
}
