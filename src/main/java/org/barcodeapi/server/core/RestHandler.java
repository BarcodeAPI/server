package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.limits.ClientLimiter;
import org.barcodeapi.server.limits.LimiterCache;
import org.barcodeapi.server.session.CachedSession;
import org.barcodeapi.server.session.SessionCache;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasHashes;
import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

public abstract class RestHandler extends AbstractHandler {

	private final String _NAME;

	private final String serverName;

	private final LibMetrics stats;

	private final boolean apiAuthRequired;

	private final boolean apiRateLimited;

	public RestHandler(boolean authRequired, boolean rateLimited) {
		LibMetrics.hitMethodRunCounter();

		// extract class name
		String className = getClass().getName();
		_NAME = className.substring(className.lastIndexOf('.') + 1);

		try {
			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		this.stats = LibMetrics.instance();
		this.apiAuthRequired = authRequired;
		this.apiRateLimited = rateLimited;
	}

	public LibMetrics getStats() {
		return stats;
	}

	public boolean authRequired() {
		return apiAuthRequired;
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		long timeStart = System.currentTimeMillis();

		String method = request.getMethod();
		getStats().hitCounter("request", "count");
		getStats().hitCounter("request", "method", method);
		getStats().hitCounter("request", "target", _NAME, "count");
		getStats().hitCounter("request", "target", _NAME, "method", method);

		// skip if already handled
		if (!baseRequest.isHandled()) {
			baseRequest.setHandled(true);
		} else {
			return;
		}

		// update scheme is via proxy
		String proto = request.getHeader("X-Forwarded-Proto");
		if (proto != null) {
			baseRequest.getMetaData().getURI().setScheme(proto);
		}

		// get source of the request
		String ref = request.getHeader("Referer");
		String source = (ref != null) ? ref : "API";

		// get user IP address
		String ip = request.getRemoteAddr();
		String fwd = request.getHeader("X-Forwarded-For");
		String from = (fwd != null) ? (fwd + " : " + ip) : ip;

		// set response code
		response.setStatus(HttpServletResponse.SC_OK);

		// log the request
		LibLog.clogF("request", "I4001", _NAME, target, source, from);

		// server details
		addCORSHeaders(baseRequest, response);
		response.setHeader("Server", "BarcodeAPI.org");
		response.setHeader("Server-Node", serverName);
		response.setHeader("Accept-Charset", "utf-8");
		response.setCharacterEncoding("UTF-8");

		// done if only options
		if (method.equals("OPTIONS")) {
			return;
		}

		// get limiter by API key or IP
		String key = request.getParameter("key");
		ClientLimiter limiter = (key != null) ? //
				LimiterCache.getByKey(key) : LimiterCache.getByIp(from);
		try {

			// check if allowed by rate limiter
			if (apiRateLimited && !limiter.allowRequest()) {
				getStats().hitCounter("request", "limited");
				getStats().hitCounter("request", "target", _NAME, "limited");
				LibLog._clogF("E0609", limiter.getCaller());
				response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
				return;
			}
		} finally {

			// allow user to see their token count
			response.setHeader("X-RateLimit-Tokens", //
					String.format("%.2f", limiter.numTokens()));
		}

		// get user session info
		CachedSession session = getSession(request);
		session.hit(baseRequest.getOriginalURI().toString());
		response.addCookie(session.getCookie());

		// authenticate the user if required
		if (apiAuthRequired && !validateAdmin(request)) {

			getStats().hitCounter("request", "authfail");
			getStats().hitCounter("request", "target", _NAME, "authfail");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("WWW-Authenticate", "Basic realm=BarcodeAPI.org Admin API");
			return;
		}

		try {

			// call the implemented method
			String uri = baseRequest.getOriginalURI();
			this.onRequest(uri, request, response);
		} catch (Exception e) {

			// TODO handle this
			e.printStackTrace();
		}

		// hit the counters
		long targetTime = System.currentTimeMillis() - timeStart;
		getStats().hitCounter(targetTime, "request", "time");
		getStats().hitCounter(targetTime, "request", "target", _NAME, "time");
	}

	protected abstract void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	protected void addCORSHeaders(HttpServletRequest request, HttpServletResponse response) {

		String origin = request.getHeader("origin");
		if (origin != null) {

			response.setHeader("Access-Control-Max-Age", "86400");
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		if (request.getMethod().equals("OPTIONS")) {
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		}
	}

	protected boolean validateAdmin(HttpServletRequest request) {

		// false if no authentication
		String auth = request.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Basic")) {
			return false;
		}

		String authString = auth.substring(6);
		String decode = new String(Base64.getDecoder().decode(authString));
		String[] unpw = decode.split(":");

		String uName = unpw[0];
		String passHash = LibExtrasHashes.sumSHA256(unpw[1].getBytes());

		JSONObject admins = AppConfig.get().getJSONObject("admins");
		return admins.getString(uName).equals(passHash);
	}

	protected CachedSession getSession(HttpServletRequest request) {

		// get existing user session
		CachedSession session = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("session")) {
					session = SessionCache.getSession(cookie.getValue());
				}
			}
		}

		// new session if none existing
		if (session == null) {
			session = SessionCache.createNewSession();
		}

		return session;
	}
}
