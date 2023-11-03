package org.barcodeapi.server.limits;

import org.barcodeapi.server.core.AppConfig;
import org.barcodeapi.server.core.ObjectCache;
import org.json.JSONObject;

import com.mclarkdev.tools.libmetrics.LibMetrics;

public class LimiterCache {

	private static final String CACHE_IP = "LIMITS-IP";

	public static ObjectCache getIpCache() {
		return ObjectCache.getCache(CACHE_IP);
	}

	public static ClientLimiter getByIp(String caller) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache cache = ObjectCache.getCache(CACHE_IP);
		if (!cache.has(caller)) {
			cache.put(caller, newByIp(caller));
		}

		return (ClientLimiter) cache.get(caller);
	}

	private static ClientLimiter newByIp(String caller) {
		JSONObject limits = AppConfig.get().getJSONObject("limits");

		// Check if rate limiter is enforced
		if (!limits.getBoolean("enforce")) {
			return new ClientLimiter(caller, -1);
		}

		long appLimit = limits.getLong("default");

		JSONObject ips = limits.getJSONObject("ips");

		long usrLimit = ips.optLong(caller, appLimit);

		return new ClientLimiter(caller, usrLimit);
	}

	private static final String CACHE_KEY = "LIMITS-KEY";

	public static ObjectCache getKeyCache() {
		return ObjectCache.getCache(CACHE_KEY);
	}

	public static ClientLimiter getByKey(String caller) {
		LibMetrics.hitMethodRunCounter();

		ObjectCache cache = ObjectCache.getCache(CACHE_KEY);
		if (!cache.has(caller)) {
			cache.put(caller, newByKey(caller));
		}

		return (ClientLimiter) cache.get(caller);
	}

	private static ClientLimiter newByKey(String caller) {
		JSONObject limits = AppConfig.get().getJSONObject("limits");

		// Check if rate limiter is enforced
		if (!limits.getBoolean("enforce")) {
			return new ClientLimiter(caller, -1);
		}

		JSONObject keys = limits.getJSONObject("keys");

		long usrLimit = keys.optLong(caller, 0);

		return new ClientLimiter(caller, usrLimit);
	}
}
