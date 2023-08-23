package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.core.utils.StringUtils;
import org.barcodeapi.server.session.CachedSession;
import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.mclarkdev.tools.liblog.LibLog;

public abstract class RestHandler extends AbstractHandler {

	private final String _NAME;

	private final String serverName;

	private final StatsCollector stats;

	private final boolean authRequired;

	public RestHandler(boolean authRequired) {

		// extract class name
		String className = getClass().getName();
		_NAME = className.substring(className.lastIndexOf('.') + 1);

		try {
			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		this.stats = StatsCollector.getInstance();
		this.authRequired = authRequired;
	}

	public StatsCollector getStats() {
		return stats;
	}

	public boolean authRequired() {
		return authRequired;
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		long timeStart = System.currentTimeMillis();
		getStats().hitCounter("request", "count");
		getStats().hitCounter("request", "method", request.getMethod());
		getStats().hitCounter("request", "target", _NAME, "count");
		getStats().hitCounter("request", "target", _NAME, "method", request.getMethod());

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
		String source;
		String ref = request.getHeader("Referer");
		if (ref != null) {
			source = ref;
		} else {
			source = "API";
		}

		// get users IP
		String from;
		String via = request.getRemoteAddr();
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null) {
			from = ip + " : " + via;
		} else {
			from = via;
		}

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

		// user session info
		CachedSession session = getSession(request);
		session.hit(baseRequest.getOriginalURI().toString());
		response.addCookie(session.getCookie());

		// done if only options
		if (request.getMethod().equals("OPTIONS")) {
			return;
		}

		// authenticate the user if required
		if (authRequired() && !validateAdmin(request)) {

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

		String passHash = StringUtils.sumSHA256(unpw[1].getBytes());
		String userAuth = String.format("%s:%s", unpw[0], passHash);

		return Authlist.getAuthlist().contains(userAuth);
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
