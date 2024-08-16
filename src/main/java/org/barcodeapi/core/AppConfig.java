package org.barcodeapi.core;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasStreams;
import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * AppConfig.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class AppConfig {

	private static JSONObject appConfig;

	static {
		reload();
	}

	public static void reload() {
		try {
			File confFile = new File("config", "app.json");
			String raw = LibExtrasStreams.readFile(confFile);
			appConfig = new JSONObject(raw);
		} catch (IOException e) {
			throw LibLog._clog("E0006", e).asException();
		}
	}

	/**
	 * 
	 * @return the app config
	 */
	public static JSONObject get() {
		LibMetrics.hitMethodRunCounter();

		return appConfig;
	}
}
