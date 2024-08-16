package org.barcodeapi.server.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasStreams;
import com.mclarkdev.tools.liblog.LibLog;

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

	public CodeType loadType(String name) {

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

	public Set<String> getTypes() {

		return codeTypes.keySet();
	}

	public CodeType getType(String name) {

		return codeTypes.get(name);
	}
}
