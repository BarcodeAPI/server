package org.barcodeapi.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.barcodeapi.server.statistics.StatsCollector;

public class ServerRuntime {

	// System runtime identifier
	private static final String _RUNTIME_ID;
	private static final long _RUNTIME_TIMESTART;
	private static final String _RUNTIME_VERSION;
	private static final String _RUNTIME_HOST;

	static {

		_RUNTIME_ID = UUID.randomUUID().toString();
		StatsCollector.getInstance()//
				.setValue(_RUNTIME_ID, "system", "runtimeId");

		_RUNTIME_TIMESTART = System.currentTimeMillis();
		StatsCollector.getInstance()//
				.setValue(_RUNTIME_TIMESTART, "system", "time", "start");

		_RUNTIME_VERSION = "";
		StatsCollector.getInstance()//
				.setValue(_RUNTIME_VERSION, "system", "version");

		try {
			_RUNTIME_HOST = InetAddress.getLocalHost().getCanonicalHostName();
			StatsCollector.getInstance()//
					.setValue(_RUNTIME_HOST, "system", "host");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public static final String getRuntimeID() {
		return _RUNTIME_ID;
	}

	public static final long getTimeStart() {
		return _RUNTIME_TIMESTART;
	}

	public static final long getTimeRunning() {
		return System.currentTimeMillis() - getTimeStart();
	}

	public static final String getVersion() {
		return _RUNTIME_VERSION;
	}

	public static final String getHostname() {
		return _RUNTIME_HOST;
	}
}
