package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.session.CachedSession;
import org.barcodeapi.server.session.SessionCache;
import org.barcodeapi.server.statistics.StatsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class RestHandler extends AbstractHandler {

	private final String _NAME;

	private final String serverName;

	private final StatsCollector stats;

	public RestHandler() {

		// extract class name
		String className = getClass().getName();
		_NAME = className.substring(className.lastIndexOf('.') + 1);

		try {
			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		this.stats = StatsCollector.getInstance();
	}

	public StatsCollector getStats() {
		return stats;
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// skip if handled
		if (baseRequest.isHandled()) {
			return;
		}

		// request is handled
		baseRequest.setHandled(true);
		response.setStatus(HttpServletResponse.SC_OK);

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

		// log the request
		Log.out(LOG.REQUEST, _NAME + " : " + target + " : " + source + " : " + from);

		// hit the counters
		getStats().incrementCounter("request.count.total");
		getStats().incrementCounter("request.count." + _NAME);
		getStats().incrementCounter("request.method." + request.getMethod());

		// add CORS headers
		addCORS(baseRequest, response);

		// TODO make this end the call
		if (request.getMethod().equals("OPTIONS")) {
			return;
		}

		// server details
		response.setHeader("Server", "BarcodeAPI.org");
		response.setHeader("Server-Node", serverName);
		response.setHeader("Accept-Charset", "utf-8");
		response.setCharacterEncoding("UTF-8");

		// user session info
		CachedSession session = getSession(request);
		session.hit(baseRequest.getOriginalURI().toString());
		response.addCookie(session.getCookie());
	}

	protected void addCORS(HttpServletRequest request, HttpServletResponse response) {

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
