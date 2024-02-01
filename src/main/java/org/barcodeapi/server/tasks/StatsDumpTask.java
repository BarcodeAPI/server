package org.barcodeapi.server.tasks;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.libargs.LibArgs;
import com.mclarkdev.tools.liblog.LibLog;

public class StatsDumpTask extends BackgroundTask {

	private static final String _TELEM_TARGET = "https://barcodeapi.org/stats/upload";
	private static final boolean _TELEM_ENABLED = (!LibArgs.instance().getBoolean("no-telemetry"));

	public StatsDumpTask() {
		super();
	}

	@Override
	public void onRun() {

		// get and print metric data
		String data = getStats().getDetails().toString();
		LibLog.log("stats", data);

		// upload metrics if enabled
		if (_TELEM_ENABLED) {

			LibLog._clog("I2604");

			try {

				// create http client
				URL url = new URL(_TELEM_TARGET);
				URLConnection con = url.openConnection();
				HttpURLConnection http = (HttpURLConnection) con;

				// set request options
				http.setDoOutput(true);
				http.setRequestMethod("POST");
				http.setFixedLengthStreamingMode(data.length());
				http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

				// connect and write
				http.connect();
				http.getOutputStream().write(data.getBytes());
				http.getResponseCode();

			} catch (Exception e) {

				LibLog._clog("E2609", e);
			}
		}
	}
}
