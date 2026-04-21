package org.barcodeapi.server.tasks;

import java.util.Map;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.Tokens;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LimiterMintingTask.java
 * 
 * A background task which periodically mints rate limiting tokens.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class LimiterMintingTask extends BackgroundTask {

	public LimiterMintingTask() {
		super();
	}

	@Override
	public void onRun() {
		double tokensMinted = 0;

		// Lookup the requested cache
		ObjectCache cache = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);

		// Loop each cache entry
		int mintCount = 0;
		for (Map.Entry<String, CachedObject> entry : cache.raw().entrySet()) {

			// Get the limiters token object
			CachedLimiter limiter = ((CachedLimiter) entry.getValue());
			Tokens tokens = limiter.getTokens();

			// Mint the tokens
			tokensMinted += tokens.mint();
			mintCount += 1;
		}

		// Log number of tokens minted
		LibLog._clogF("I2621", tokensMinted, mintCount);
		getStats().hitCounter(tokensMinted, "task", getName(), "minted");
	}
}
