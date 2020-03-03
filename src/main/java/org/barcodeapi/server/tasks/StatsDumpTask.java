package org.barcodeapi.server.tasks;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;
import org.barcodeapi.server.core.BackgroundTask;

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
