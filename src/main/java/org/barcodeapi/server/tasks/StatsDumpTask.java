package org.barcodeapi.server.tasks;

import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;

public class StatsDumpTask extends BackgroundTask {

	public StatsDumpTask() {
		super();
	}

	@Override
	public void onRun() {

		// print stats to the log
		Log.out(LOG.SERVER, "STATS : " + getStats().dumpJSON().toString());
	}
}
