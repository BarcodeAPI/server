package org.barcodeapi.server.core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

public class Authlist {

	private static List<String> authlist;

	static {
		reload();
	}

	public static void reload() {
		LibMetrics.hitMethodRunCounter();

		try {

			authlist = Files.readAllLines(Paths.get("config/authlist.conf"));
		} catch (Exception e) {

			throw LibLog._clog("E0799").asException(IllegalStateException.class);
		}
	}

	public static List<String> getAuthlist() {
		LibMetrics.hitMethodRunCounter();

		return authlist;
	}
}
