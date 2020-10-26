package org.barcodeapi.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class ServerRuntime {

	// System runtime identifier
	private static final String _RUNTIME_ID;
	private static final long _RUNTIME_TIMESTART;
	private static final String _RUNTIME_VERSION;
	private static final String _RUNTIME_HOST;

	static {

		_RUNTIME_ID = UUID.randomUUID().toString();
		_RUNTIME_TIMESTART = System.currentTimeMillis();
		_RUNTIME_VERSION = "";

		try {
			_RUNTIME_HOST = InetAddress.getLocalHost().getCanonicalHostName();
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
