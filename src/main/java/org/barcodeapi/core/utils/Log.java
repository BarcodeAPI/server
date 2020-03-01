package org.barcodeapi.core.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Log {

	public enum LOG {
		SERVER, REQUEST, BARCODE, ERROR;
	}

	private static final HashMap<LOG, PrintWriter> logFiles;

	static {

		logFiles = new HashMap<>();
		for (LOG log : LOG.values()) {
			File f = new File("log", log.name() + ".log");
			try {
				logFiles.put(log, new PrintWriter(new FileWriter(f)));
			} catch (Exception e) {
				System.err.println("Failed to setup log: " + f.getAbsolutePath());
			}
		}
	}

	private static final SimpleDateFormat _DFORMAT = new SimpleDateFormat("yyMMddHHmmssZ");

	public static void out(LOG type, String message) {

		// format log line
		String time = _DFORMAT.format(Calendar.getInstance().getTime());
		String output = time + " : " + type.toString() + " : " + message;

		// write to log file and out
		logFiles.get(type).println(output);
		System.out.println(output);
	}
}
