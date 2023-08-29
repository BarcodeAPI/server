package org.barcodeapi.server.core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.mclarkdev.tools.liblog.LibLog;

public class Authlist {

	private static List<String> authlist;

	static {
		reload();
	}

	public static void reload() {

		try {

			authlist = Files.readAllLines(Paths.get("config/authlist.conf"));
		} catch (Exception e) {

			throw new RuntimeException(LibLog._clog("E0799").toString());
		}
	}

	public static List<String> getAuthlist() {

		return authlist;
	}
}
