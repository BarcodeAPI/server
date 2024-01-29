package org.barcodeapi.server.core;

import org.json.JSONArray;
import org.json.JSONObject;

public class CodeType {

	private final String GEN_ROOT = "org.barcodeapi.server.gen.types";

	private final JSONObject config;

	private final String name;
	private final String generator;
	private final int threads;

	private final int priority;
	private final String patternA;
	private final String patternE;

	private final String[] targets;

	private final String example;

	private final JSONObject description;

	private CodeType(JSONObject config) {
		this.config = config;

		// generator name
		this.name = config.getString("name");

		// generator config
		this.generator = (GEN_ROOT + config.getString("generator"));
		this.threads = config.getInt("threads");

		// get barcode patterns
		this.priority = config.getInt("priority");
		this.patternA = config.getJSONObject("pattern").getString("auto");
		this.patternE = config.getJSONObject("pattern").getString("extended");

		// setup list of targets
		JSONArray target = config.getJSONArray("target");
		this.targets = new String[target.length()];
		for (int x = 0; x < target.length(); x++) {
			this.targets[x] = target.getString(x);
		}

		this.example = config.getString("example");

		this.description = config.getJSONObject("description");
	}

	public JSONObject getConfig() {

		return config;
	}

	public String getName() {
		return name;
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

	public String[] getTargets() {
		return targets;
	}

	public int getCostBasic() {
		return 1;
	}

	public int getCostCustom() {
		return 20;
	}

	private String getExample() {
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
				.put("pattern", type.getPatternExtended())//
				.put("example", type.getExample())//
				.put("targets", new JSONArray(type.getTargets()))//
				.put("description", type.getDescription());
	}

}
