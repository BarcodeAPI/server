package org.barcodeapi.server.core;

import com.mclarkdev.tools.liblog.LibLog;
//import com.mclarkdev.tools.libloggelf.LibLogGELF;

public class Log {

	public enum LOG {
		SERVER, REQUEST, BARCODE, ERROR;
	}

	public static final boolean diskEnabled;
	public static final boolean gelfEnabled;

	static {

		diskEnabled = (System.getenv("LOG_DISK_DISABLE") == null);
		gelfEnabled = (System.getenv("LOG_GELF_SERVER") != null);
	}

	public static void out(LOG type, String message) {

		// write to log file
		if (diskEnabled) {
			LibLog.log(type.toString(), message);
		}

		// write to network
//		if (gelfEnabled) {
//			LibLogGELF.write(message, null, 1, "type", type.toString());
//		}
	}
}
