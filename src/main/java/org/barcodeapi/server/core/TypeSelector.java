package org.barcodeapi.server.core;

import java.util.HashMap;

import com.mclarkdev.tools.libmetrics.LibMetrics;

public class TypeSelector {

	private static HashMap<String, CodeType> typeCache = new HashMap<>();

	static {

		// Loop all known types
		CodeTypes types = CodeTypes.inst();
		for (String name : types.getTypes()) {

			// Add types strings to cache
			CodeType type = types.getType(name);
			for (String target : type.getTargets()) {
				typeCache.put(target.toLowerCase(), type);
			}
		}
	}

	/**
	 * Get a CodeType object by any of its associated string IDs.
	 * 
	 * Will return null if none are found.
	 * 
	 * @param codeType
	 * @return
	 */
	public static CodeType getTypeFromString(String codeType) {
		LibMetrics.hitMethodRunCounter();

		return typeCache.get(codeType.toLowerCase());
	}

	/**
	 * Returns a CodeType object best suited for the given data string.
	 * 
	 * @param data
	 * @return
	 */
	public static CodeType getTypeFromData(String data) {
		LibMetrics.hitMethodRunCounter();

		CodeTypes types = CodeTypes.inst();
		for (String name : types.getTypes()) {
			CodeType type = types.getType(name);
			if (data.matches(type.getPatternAuto())) {
				return type;
			}
		}

		return null;
	}
}
