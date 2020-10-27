package org.barcodeapi.server.core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Authlist {

	private static List<String> authlist;

	static {
		reload();
	}

	public static void reload() {

		try {

			authlist = Files.readAllLines(Paths.get("config/authlist.conf"));
		} catch (Exception e) {

			throw new RuntimeException("Failed to initialize authlist.");
		}
	}

	public static List<String> getAuthlist() {

		return authlist;
	}
}
