package org.barcodeapi.server.tasks;

import java.util.Map;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterMintingTask.java
 * 
 * A background task which periodically mints rate limiting tokens.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class LimiterMintingTask extends BackgroundTask {

	public LimiterMintingTask() {
		super();
	}

	@Override
	public void onRun() {

		double tokensMinted = 0;

		tokensMinted += mintTokens(ObjectCache.CACHE_IP);
		tokensMinted += mintTokens(ObjectCache.CACHE_KEY);

		LibLog._clogF("I2621", tokensMinted);
		getStats().hitCounter(tokensMinted, "task", getName(), "minted");
	}

	private double mintTokens(String cacheName) {

		ObjectCache cache = ObjectCache.getCache(cacheName);

		double tokensMinted = 0;
		for (Map.Entry<String, CachedObject> entry : cache.raw().entrySet()) {

			// mint the limiter tokens
			tokensMinted += ((CachedLimiter) entry.getValue()).mintTokens();
		}

		return tokensMinted;
	}
}
