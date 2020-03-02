package org.barcodeapi.server.core;

import java.util.TimerTask;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;

public abstract class BackgroundTask extends TimerTask {

	@Override
	public void run() {

		Log.out(LOG.SERVER, "TASK : " + getClass().getName());
		onRun();
	}

	public abstract void onRun();
}
