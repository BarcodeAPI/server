package org.barcodeapi.server.gen;

import org.barcodeapi.core.utils.StringUtils;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarcodeRequest {

	private static final Pattern uriPattern;

	static {
		Set<String> validTypes = new HashSet<>();
		validTypes.add("auto");
		for (CodeType codeType : CodeType.values()) {
			validTypes.add(codeType.name());
			validTypes.addAll(Arrays.asList(codeType.getTypeStrings()));
		}

		uriPattern = Pattern.compile("(?:/|^)(" + String.join("|", validTypes) + ")/([^?]+)(?:\\?(.*))?");
	}

	private final CodeType type;
	private final String data;
	private final JSONObject options;

	private BarcodeRequest(CodeType type, String data, JSONObject options) {
		this.type = type;
		this.data = data;
		this.options = options;
	}

	public static BarcodeRequest fromUri(String uri) throws GenerationException {
		Matcher uriMatcher = uriPattern.matcher(uri);
		if (!uriMatcher.find()) {
			throw new GenerationException(GenerationException.ExceptionType.INVALID, "Invalid uri. Should have the format /<type>/<barcode>");
		}

		// Get the type
		CodeType type = TypeSelector.getType(uriMatcher.group(1), uriMatcher.group(2));
		if (type == null) {
			throw new GenerationException(GenerationException.ExceptionType.INVALID, "Invalid type: " + uriMatcher.group(1));
		}

		// get and parse options
		JSONObject options = new JSONObject();
		if (uriMatcher.groupCount() >= 4 && uriMatcher.group(3) != null) {
			options = StringUtils.parseOptions(uriMatcher.group(3));
		}

		return new BarcodeRequest(type, uriMatcher.group(2), options);
	}

	public static BarcodeRequest fromJson(JSONObject json) throws GenerationException {
		CodeType type = TypeSelector.getTypeFromString(json.getString("type"));
		if (type == null) {
			throw new GenerationException(GenerationException.ExceptionType.INVALID, "Invalid type: " + json.getString("type"));
		}
		JSONObject options = json.optJSONObject("options");
		return new BarcodeRequest(
			type,
			json.getString("data"),
			options == null ? new JSONObject() : options
		);
	}

	public CodeType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public boolean useCache() {
		return this.options.optBoolean("cached", false);
	}

	public JSONObject getOptions() {
		return options;
	}
}
