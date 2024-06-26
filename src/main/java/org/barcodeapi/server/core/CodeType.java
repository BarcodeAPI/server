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

	private final String generator;
	private final int threads;

	private final int priority;
	private final String patternA;
	private final String patternE;

	private final boolean nonprinting;

	private final String[] targets;

	private final int costBasic;
	private final int costCustom;

	private final String example;
	private final JSONObject description;

	private CodeType(JSONObject config) {
		this.config = config;

		// generator name
		this.name = config.getString("name");
		this.display = config.getString("display");

		// generator config
		this.generator = (GEN_ROOT + config.getString("generator"));
		this.threads = config.getInt("threads");

		// get barcode patterns
		this.priority = config.getInt("priority");
		this.patternA = config.getJSONObject("pattern").getString("auto");
		this.patternE = config.getJSONObject("pattern").getString("extended");

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
	}

	public JSONObject getConfig() {

		return config;
	}

	public String getName() {
		return name;
	}

	public String getDisplayNme() {
		return display;
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

	public static CodeType fromJSON(JSONObject conf) {

		return new CodeType(conf);
	}

	public static JSONObject toJSON(CodeType type) {
		return new JSONObject()//
				.put("name", type.getName())//
				.put("display", type.getDisplayNme())//
				.put("pattern", type.getPatternExtended())//
				.put("example", type.getExample())//
				.put("nonprinting", type.getAllowNonprinting())//
				.put("costBasic", type.getCostBasic())//
				.put("costCustom", type.getCostCustom())//
				.put("targets", new JSONArray(type.getTargets()))//
				.put("description", type.getDescription());
	}
}
