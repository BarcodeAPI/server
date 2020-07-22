package org.barcodeapi.server.gen;

import org.barcodeapi.core.utils.StringUtils;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONObject;

public class BarcodeRequest {

	private final CodeType type;
	private final String data;
	private final boolean cached;
	private final JSONObject options;

	private BarcodeRequest(CodeType type, String data, boolean cached, JSONObject options) {
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

	public static BarcodeRequest fromURI(String target) throws GenerationException {

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
			options = parseOptions(parts[1]);
		}

		// use cache based on options
		boolean useCache = true;
		if (options.length() > 0) {
			useCache = false;
		}
		if (data.length() > 64) {
			useCache = false;
		}
		if (options.optBoolean("no-cache", false)) {
			useCache = false;
		}

		CodeType type;

		// parse code type / data string
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

		return new BarcodeRequest(type, data, useCache, options);
	}

	public static BarcodeRequest fromCSV(String[] record) throws GenerationException {

		String type = "auto";
		if (record.length > 1) {
			type = record[1];
		}

		String uri = "/api/" + type + "/" + record[0];

		return BarcodeRequest.fromURI(uri);
	}

	private static JSONObject parseOptions(String opts) {

		JSONObject options = new JSONObject();

		String[] parts = opts.split("&");

		for (String option : parts) {

			String[] kv = option.split("=");
			options.put(kv[0], kv[1]);
		}

		return options;
	}
}
