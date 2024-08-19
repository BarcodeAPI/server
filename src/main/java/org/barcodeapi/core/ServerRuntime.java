package org.barcodeapi.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.UUID;

import com.mclarkdev.tools.libextras.LibExtrasStreams;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * ServerRuntime.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
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
		LibMetrics.instance().setValue(_RUNTIME_ID, "system", "runtimeId");

		_RUNTIME_TIMESTART = System.currentTimeMillis();
		LibMetrics.instance().setValue(_RUNTIME_TIMESTART, "system", "time", "start");

		try {
			_RUNTIME_VERSION = Integer.parseInt(LibExtrasStreams.readStream(//
					ServerRuntime.class.getResourceAsStream("/app.version")));
			LibMetrics.instance().setValue(_RUNTIME_VERSION, "system", "version");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			_RUNTIME_HOST = InetAddress.getLocalHost().getCanonicalHostName();
			LibMetrics.instance().setValue(_RUNTIME_HOST, "system", "host");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

		_SYS_TIMER = new Timer();
	}

	/**
	 * Returns the current runtime ID of the server.
	 * 
	 * @return the current runtime ID of the server
	 */
	public static final String getRuntimeID() {
		return _RUNTIME_ID;
	}

	/**
	 * Returns the time, in milliseconds, the server was started.
	 * 
	 * @return the time the server was started
	 */
	public static final long getTimeStart() {
		return _RUNTIME_TIMESTART;
	}

	/**
	 * Returns the amount of time, in milliseconds, the server has been running.
	 * 
	 * @return the amount of time the server has been running
	 */
	public static final long getTimeRunning() {
		return System.currentTimeMillis() - getTimeStart();
	}

	/**
	 * Returns the build version of the server.
	 * 
	 * @return the build version of the server
	 */
	public static final int getVersion() {
		return _RUNTIME_VERSION;
	}

	/**
	 * Returns the system host name.
	 * 
	 * @return the system host name
	 */
	public static final String getHostname() {
		return _RUNTIME_HOST;
	}

	/**
	 * Returns an instance of the system timer.
	 * 
	 * @return an instance of the system timer
	 */
	public static final Timer getSystemTimer() {
		return _SYS_TIMER;
	}
}
