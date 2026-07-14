package org.barcodeapi.server.tasks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedObject;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * ReportingTask.java
 * 
 * Periodic background task which
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class ReportingTask extends BackgroundTask {

	private final ConcurrentHashMap<String, Long> lastCounts;

	public ReportingTask() {
		super();

		lastCounts = new ConcurrentHashMap<>();
	}

	@Override
	public void onRun() {

		// Lookup the requested cache
		ObjectCache cache = ObjectCache.getCache(ObjectCache.CACHE_LIMITERS);

		// Loop each cache entry
		for (Map.Entry<String, CachedObject> entry : cache.raw().entrySet()) {

			String callerID = entry.getKey();
			CachedLimiter limiter = (CachedLimiter) entry.getValue();

			long countLast = lastCounts.containsKey(callerID) ? lastCounts.get(callerID) : 0;
			long countNow = limiter.getAccessCount();

			int countDiff = (int) (countNow - countLast);

			lastCounts.put(callerID, countNow);

			LibLog.logF("report", "ip:%s -> %d", callerID, countDiff);
		}
	}
}
