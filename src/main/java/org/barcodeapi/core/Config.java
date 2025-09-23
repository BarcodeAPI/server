package org.barcodeapi.core;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.mclarkdev.tools.libargs.LibArgs;
import com.mclarkdev.tools.libextras.LibExtrasStreams;
import com.mclarkdev.tools.liblog.LibLog;

public class Config {

	public enum Cfg {
		App, Blacklist, Admins, Plans, Subscribers;
	}

	private static final String dist = LibArgs.instance().getString("config", "community");

	private static final Map<Cfg, JSONObject> configs = new ConcurrentHashMap<Cfg, JSONObject>();

	/**
	 * Retrieves the name of the configuration distribution currently in use.
	 * 
	 * @return name of the configuration distribution in use
	 */
	public static String dist() {
		return dist;
	}

	/**
	 * Retrieve application configuration.
	 * 
	 * @param cfg configuration type
	 * @return the cached configuration object
	 */
	public static JSONObject get(Cfg cfg) {
		return get(cfg, false);
	}

	/**
	 * Retrieve application configuration.
	 * 
	 * @param cfg         configuration type
	 * @param forceReload force reload from disk
	 * @return the cached configuration object
	 */
	public static JSONObject get(Cfg cfg, boolean forceReload) {

		// Check if already loaded or forcing a reload
		if ((!configs.containsKey(cfg)) || forceReload) {

			// Reload from disk and add to map
			configs.put(cfg, loadConfig(cfg));
		}

		// Return the cached data
		return configs.get(cfg);
	}

	/**
	 * Load a given configuration file from disk.
	 * 
	 * @param cfg configuration type
	 * @return the loaded configuration object
	 */
	private static final JSONObject loadConfig(Cfg cfg) {

		try {

			// Determine name of file loaded
			String confName = (cfg.toString().toLowerCase());

			// Find file on disk
			File confFile = new File(//
					new File("config", dist), //
					(confName + ".json"));

			// Read the file as a raw string
			String raw = LibExtrasStreams.readFile(confFile);

			// Return string parsed as JSON
			return new JSONObject(raw);

		} catch (Error | Exception e) {

			// Log general exception
			throw new Error("Failed loading app config.", e);
		}
	}
}
