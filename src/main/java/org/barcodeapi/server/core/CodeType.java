package org.barcodeapi.server.core;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CodeType.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CodeType {

	private final String GEN_ROOT = "org.barcodeapi.server.gen.types";

	private final JSONObject config;

	private final String name;
	private final String display;

	private final boolean show;

	private final String generator;
	private final int threads;

	private final int priority;
	private final String patternA;
	private final String patternE;

	private final boolean checksum;
	private final boolean nonprinting;

	private final String[] targets;

	private final int costBasic;
	private final int costCustom;

	private final String example;
	private final JSONObject description;
	private final JSONObject wiki;

	private final JSONArray options;

	private CodeType(JSONObject config) {
		this.config = config;

		// generator name
		this.name = config.getString("name");
		this.display = config.getString("display");

		// show on web UI
		this.show = config.getBoolean("show");

		// generator config
		this.generator = (GEN_ROOT + config.getString("generator"));
		this.threads = config.getInt("threads");

		// get barcode patterns
		this.priority = config.getInt("priority");
		this.patternA = config.getJSONObject("pattern").getString("auto");
		this.patternE = config.getJSONObject("pattern").getString("extended");

		// supports checksums
		this.checksum = config.getBoolean("checksum");

		// has nonprinting support
		this.nonprinting = config.getBoolean("nonprinting");

		// setup list of targets
		JSONArray target = config.getJSONArray("target");
		this.targets = new String[target.length()];
		for (int x = 0; x < target.length(); x++) {
			this.targets[x] = target.getString(x);
		}

		// get barcode costs
		JSONObject costs = config.getJSONObject("cost");
		this.costBasic = costs.getInt("basic");
		this.costCustom = costs.getInt("custom");

		// get example and description
		this.example = config.getString("example");
		this.description = config.getJSONObject("description");
		this.wiki = config.getJSONObject("wiki");

		// get available options
		this.options = config.getJSONArray("options");
	}

	public JSONObject getConfig() {

		return config;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return display;
	}

	public boolean getShowType() {
		return show;
	}

	public String getGeneratorClass() {
		return generator;
	}

	public int getNumThreads() {
		return threads;
	}

	public int getPriority() {
		return priority;
	}

	public String getPatternAuto() {
		return patternA;
	}

	public String getPatternExtended() {
		return patternE;
	}

	public boolean enforceChecksum() {
		return checksum;
	}

	public boolean getAllowNonprinting() {
		return nonprinting;
	}

	public String[] getTargets() {
		return targets;
	}

	public int getCostBasic() {
		return costBasic;
	}

	public int getCostCustom() {
		return costCustom;
	}

	public String getExample() {
		return example;
	}

	public JSONObject getDescription() {
		return description;
	}

	public JSONObject getWiki() {
		return wiki;
	}

	public JSONArray getOptions() {
		return options;
	}

	public static CodeType fromJSON(JSONObject conf) {

		return new CodeType(conf);
	}

	public static JSONObject toJSON(CodeType type) {
		return new JSONObject()//
				.put("name", type.getName())//
				.put("display", type.getDisplayName())//
				.put("show", type.getShowType())//
				.put("pattern", type.getPatternExtended())//
				.put("example", type.getExample())//
				.put("checksum", type.enforceChecksum())//
				.put("nonprinting", type.getAllowNonprinting())//
				.put("costBasic", type.getCostBasic())//
				.put("costCustom", type.getCostCustom())//
				.put("targets", new JSONArray(type.getTargets()))//
				.put("description", type.getDescription())//
				.put("wiki", type.getWiki())//
				.put("options", type.getOptions());
	}
}
