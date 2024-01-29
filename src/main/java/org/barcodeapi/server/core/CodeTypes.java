package org.barcodeapi.server.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

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

		try {
			// read config file, parse as json
			JSONObject typeConfig = new JSONObject(//
					LibExtrasStreams.readFile(//
							new File(typeDir, (name + ".json"))));

			CodeType codeType = CodeType.fromJSON(typeConfig);
			codeTypes.put(name, codeType);
			return codeType;

		} catch (IOException e) {

			throw LibLog._log("Failed loading type config.", e).asException();
		}
	}

	public Set<String> getTypes() {

		return codeTypes.keySet();
	}

	public CodeType getType(String name) {

		return codeTypes.get(name);
	}
}
