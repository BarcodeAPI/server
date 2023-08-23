package org.barcodeapi.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.UUID;

import org.barcodeapi.server.statistics.StatsCollector;

import com.mclarkdev.tools.libextras.LibExtrasStreams;

public class ServerRuntime {

	// System runtime information
	private static final String _RUNTIME_ID;
	private static final long _RUNTIME_TIMESTART;
	private static final int _RUNTIME_VERSION;
	private static final String _RUNTIME_HOST;

	// Background task timer
	private static final Timer _SYS_TIMER;

	static {

		_RUNTIME_ID = UUID.randomUUID().toString();
		StatsCollector.getInstance().setValue(_RUNTIME_ID, "system", "runtimeId");

		_RUNTIME_TIMESTART = System.currentTimeMillis();
		StatsCollector.getInstance().setValue(_RUNTIME_TIMESTART, "system", "time", "start");

		try {
			_RUNTIME_VERSION = Integer.parseInt(LibExtrasStreams.readStream(//
					ServerRuntime.class.getResourceAsStream("/app.version")));
			StatsCollector.getInstance().setValue(_RUNTIME_VERSION, "system", "version");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			_RUNTIME_HOST = InetAddress.getLocalHost().getCanonicalHostName();
			StatsCollector.getInstance().setValue(_RUNTIME_HOST, "system", "host");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

		_SYS_TIMER = new Timer();
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

	public static final int getVersion() {
		return _RUNTIME_VERSION;
	}

	public static final String getHostname() {
		return _RUNTIME_HOST;
	}

	public static final Timer getSystemTimer() {
		return _SYS_TIMER;
	}
}
