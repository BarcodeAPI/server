package org.barcodeapi.server.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasStreams;
import com.mclarkdev.tools.liblog.LibLog;

/**
 * CodeTypes.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CodeTypes {

	private static final CodeTypes typeCache = new CodeTypes();

	public static CodeTypes inst() {
		return typeCache;
	}

	private final File typeDir = new File("config", "types");

	private final HashMap<String, CodeType> codeTypes;

	private CodeTypes() {
		codeTypes = new HashMap<>();
	}

	/**
	 * Initializes a give CodeType from config.
	 * 
	 * @param name the name of the config file
	 * @return the loaded CodeType
	 */
	public CodeType loadType(String name) {

		// Setup and log config file name
		File typeFile = new File(typeDir, (name + ".json"));
		LibLog._clogF("I0061", typeFile.getPath());

		try {

			// Read config file, parse as JSON
			JSONObject typeConfig = new JSONObject(//
					LibExtrasStreams.readFile(typeFile));

			// Initialize and log the CodeType
			CodeType codeType = CodeType.fromJSON(typeConfig);
			LibLog._clogF("I0062", codeType.getName(), codeType.getNumThreads());

			// Add to map and return the type
			codeTypes.put(name, codeType);
			return codeType;

		} catch (JSONException | IOException e) {

			// Print and throw failure loading type
			throw LibLog._clog("E0069", e).asException();
		}
	}

	/**
	 * Returns a list of all supported types.
	 * 
	 * @return a list of all supported types
	 */
	public Set<String> getTypes() {

		return codeTypes.keySet();
	}

	/**
	 * Returns the CodeType object for a give name.
	 * 
	 * @param name the name of the CodeType
	 * @return the CodeType object
	 */
	public CodeType getType(String name) {

		return codeTypes.get(name);
	}
}
