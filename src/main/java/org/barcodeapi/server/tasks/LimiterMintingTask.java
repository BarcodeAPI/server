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

		// Mint tokens for IP cache
		tokensMinted += mintTokens(ObjectCache.CACHE_IP);

		// Mint tokens for Key cache
		tokensMinted += mintTokens(ObjectCache.CACHE_KEY);

		// Log number of tokens minted
		LibLog._clogF("I2621", tokensMinted);
		getStats().hitCounter(tokensMinted, "task", getName(), "minted");
	}

	private double mintTokens(String cacheName) {
		double tokensMinted = 0;

		// Lookup the requested cache
		ObjectCache cache = ObjectCache.getCache(cacheName);

		// Loop each cache entry
		for (Map.Entry<String, CachedObject> entry : cache.raw().entrySet()) {

			// Mint tokens for the limiter
			tokensMinted += ((CachedLimiter) entry.getValue()).mintTokens();
		}

		// Return number minted
		return tokensMinted;
	}
}
