package org.barcodeapi.server.core;

import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.session.CachedSession;
import org.barcodeapi.server.session.SessionCache;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public abstract class RestHandler extends AbstractHandler {

	private final String _NAME;
	private final String serverName;

	public RestHandler() {

		// extract class name
		String className = getClass().getName();
		_NAME = className.substring(className.lastIndexOf('.') + 1);

		try {
			serverName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
		Log.out(LOG.REQUEST, String.format("%s : %s : %s : %s", _NAME, target, source, from));

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

		try {

			// call the implemented method
			this.onRequest(request, response);
		} catch (Exception e) {
			// TODO handle this
			e.printStackTrace();

			response.setStatus(500);
			response.getOutputStream().write(("Internal server error\n" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
		}
	}

	protected abstract void onRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

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
