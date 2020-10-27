package org.barcodeapi.server.core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Blacklist {

	private static List<String> blacklist;

	static {
		reload();
	}

	public static void reload() {

		try {

			blacklist = Files.readAllLines(Paths.get("config/blacklist.conf"));
		} catch (Exception e) {

			throw new RuntimeException("Failed to initialize blacklist.");
		}
	}

	public static List<String> getBlacklist() {

		return blacklist;
	}
}
