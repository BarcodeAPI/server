package org.barcodeapi.server.core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Blacklist {

	private static final List<String> blacklist;

	static {

		try {

			blacklist = Files.readAllLines(Paths.get("resources/blacklist.conf"));
		} catch (Exception e) {

			throw new RuntimeException("Failed to initialize blacklist.");
		}
	}

	public static List<String> getBlacklist() {

		return blacklist;
	}
}
