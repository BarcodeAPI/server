package org.barcodeapi.core;

import java.util.UUID;

public class ServerRuntime {

	// System runtime identifier
	private static final String _RUNTIME_ID = UUID.randomUUID().toString();

	public static final String getRuntimeID() {
		return _RUNTIME_ID;
	}

	private static final long _RUNTIME_TIMESTART = System.currentTimeMillis();

	public static final long getTimeStart() {
		return _RUNTIME_TIMESTART;
	}

	public static final long getTimeRunning() {
		return System.currentTimeMillis() - getTimeStart();
	}
}
