package org.barcodeapi.server.tasks;

import java.util.Map;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.Reputation;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * ReputationDecayTask.java
 * 
 * A background task which periodically decays a limiters reputation.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class ReputationDecayTask extends BackgroundTask {

	public ReputationDecayTask() {
		super();
	}

	@Override
	public void onRun() {

		// Lookup the requested cache
		ObjectCache cache = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);

		// Loop each cache entry
		int decayCount = 0;
		for (Map.Entry<String, CachedObject> entry : cache.raw().entrySet()) {

			// Get the limiter reputation object
			CachedLimiter limiter = ((CachedLimiter) entry.getValue());
			Reputation rep = limiter.getReputation();

			// Run decay method
			double diff = rep.decay();
			decayCount += 1;

			// Log the decay entry for abusers
			if (rep.isAbuser()) {
				LibLog.logF("reputation", //
						"Reputation decay: %s has %.4f (+%.4f)", //
						limiter.getCallerID(), rep.value(), diff);
			}
		}

		// Log number of limiters decay was run on
		LibLog._logF("Ran decay task for %d limiters.", decayCount);
	}
}
