package org.barcodeapi.server.tasks;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;

public class StatsDumpTask extends BackgroundTask {

	private static final String _TELEMETRY_URL = "https://barcodeapi.org/stats/upload";

	private final boolean telemetry;

	public StatsDumpTask(boolean telemetry) {
		super();

		this.telemetry = telemetry;
	}

	@Override
	public void onRun() {

		// get metric data
		String data = getStats().dumpJSON().toString();

		// print to the log
		Log.out(LOG.SERVER, "STATS : " + data);

		// skip if not enabled
		if (!telemetry) {
			return;
		}

		try {

			// upload metrics to server
			Log.out(LOG.SERVER, "Uploading telemetry data...");
			HttpRequest request = HttpRequest.newBuilder()//
					.POST(HttpRequest.BodyPublishers.ofString(data))//
					.uri(URI.create(_TELEMETRY_URL))//
					.build();

			HttpClient.newHttpClient()//
					.send(request, HttpResponse.BodyHandlers.ofString());

		} catch (Exception e) {

			Log.out(LOG.SERVER, "Failed to upload telemetry data: " + e.getLocalizedMessage());
		}
	}
}
