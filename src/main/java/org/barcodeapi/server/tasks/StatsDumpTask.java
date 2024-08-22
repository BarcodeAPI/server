package org.barcodeapi.server.tasks;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.libargs.LibArgs;
import com.mclarkdev.tools.liblog.LibLog;

/**
 * StatsDumpTask.java
 * 
 * A background task which periodically logs the server stats.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class StatsDumpTask extends BackgroundTask {

	private static final String _TELEM_TARGET = "https://barcodeapi.org/stats/upload";
	private static final boolean _TELEM_ENABLED = (!LibArgs.instance().getBoolean("no-telemetry"));

	public StatsDumpTask() {
		super();
	}

	@Override
	public void onRun() {

		// Get and log metrics data
		String data = getStats().getDetails().toString();
		LibLog.log("stats", data);

		// Upload if enabled
		if (_TELEM_ENABLED) {
			sendUsageReport(data);
		}
	}

	/**
	 * Send usage statistics to a remote server to monitoring.
	 * 
	 * @param data usage statistics
	 */
	private void sendUsageReport(String data) {

		LibLog._clog("I2604");

		try {

			// Create HTTP client
			URL url = new URL(_TELEM_TARGET);
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection) con;

			// Set request options
			http.setDoOutput(true);
			http.setRequestMethod("POST");
			http.setFixedLengthStreamingMode(data.length());
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			// Connect
			http.connect();

			// Write metrics data
			http.getOutputStream().write(data.getBytes());
			http.getResponseCode();

		} catch (Exception | Error e) {

			LibLog._clog("E2609", e);
		}
	}
}
