package org.barcodeapi.server.core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

public class Blacklist {

	private static List<String> blacklist;

	static {
		reload();
	}

	public static void reload() {
		LibMetrics.hitMethodRunCounter();

		try {

			blacklist = Files.readAllLines(Paths.get("config/blacklist.conf"));
		} catch (Exception e) {

			throw LibLog._clog("E0798").asException();
		}
	}

	public static List<String> getBlacklist() {
		LibMetrics.hitMethodRunCounter();

		return blacklist;
	}
}
