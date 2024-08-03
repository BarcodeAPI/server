package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;

/**
 * RestHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public abstract class RestHandler extends AbstractHandler {

	private final String _NAME;

	private final String serverName;

	private final LibMetrics stats;

	private final boolean apiAuthRequired;

	private final boolean apiRateLimited;

	private final boolean createSessions;

	public RestHandler() {
		this(false, false, true);
	}

	public RestHandler(boolean autoRequired) {
		this(autoRequired, false, true);
	}

	public RestHandler(boolean authRequired, boolean rateLimited) {
		this(authRequired, rateLimited, true);
	}

	public RestHandler(boolean authRequired, boolean rateLimited, boolean createSessions) {
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
		this.createSessions = createSessions;
	}

	public LibMetrics getStats() {
		return stats;
	}

	public boolean apiAuthRequired() {
		return apiAuthRequired;
	}

	public boolean apiRateLimited() {
		return apiRateLimited;
	}

	public boolean createSessions() {
		return createSessions;
	}

	public void _impl(String target, Request baseRequest, HttpServletRequest request, //
			HttpServletResponse response) throws IOException, ServletException {
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, //
			HttpServletResponse response) throws IOException, ServletException {

		// Build the request context
		RequestContext ctx = new RequestContext(baseRequest, createSessions);

		// Hit the counters
		getStats().hitCounter("request", "count");
		getStats().hitCounter("request", "method", ctx.getMethod());
		getStats().hitCounter("request", "target", _NAME, "count");
		getStats().hitCounter("request", "target", _NAME, "method", ctx.getMethod());

		// Skip if already handled
		if (!baseRequest.isHandled()) {
			baseRequest.setHandled(true);
		} else {
			return;
		}

		// Log the request
		LibLog.clogF("request", //
				((ctx.getProxy() == null) ? "I4001" : "I4002"), //
				_NAME, target, ctx.getSource(), ctx.getIP(), ctx.getProxy());

		// Setup default response headers
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Server", "BarcodeAPI.org");
		response.setHeader("Server-Node", serverName);
		response.setHeader("Accept-Charset", "utf-8");

		// Add session header
		if (ctx.hasSession()) {
			response.addCookie(ctx.getSession().getCookie());
		}

		// Authenticate the user if required
		if (apiAuthRequired && (!ctx.isAdmin())) {

			getStats().hitCounter("request", "authfail");
			getStats().hitCounter("request", "target", _NAME, "authfail");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("WWW-Authenticate", "Basic realm=BarcodeAPI.org Admin API");
			return;
		}

		// Add open CORS headers
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", //
				(ctx.getOrigin() != null) ? ctx.getOrigin() : "*");

		// Send token count to user
		ctx.getLimiter().touch();
		response.setHeader("X-RateLimit-Tokens", //
				String.format("%.2f", ctx.getLimiter().numTokens()));

		// Request complete if only options
		if (ctx.getMethod().equals("OPTIONS")) {
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
			return;
		}

		try {

			// Check for implementation overrides
			this._impl(target, baseRequest, request, response);

			// Call the normal method
			this.onRequest(ctx, response);
		} catch (Exception | Error e) {

			// Log any errors
			LibLog._clog("E0699", e);
		} finally {

			// Flush output buffer
			response.flushBuffer();

			// Calculate total processing time
			long runTime = System.currentTimeMillis() - ctx.getTimestamp();

			// Hit the time and status counters
			getStats().hitCounter(runTime, "request", "time");
			getStats().hitCounter(runTime, "request", "target", _NAME, "time");
			getStats().hitCounter("request", "result", ("_" + response.getStatus()));
			getStats().hitCounter("request", "target", _NAME, "result", ("_" + response.getStatus()));
		}
	}

	protected abstract void onRequest(RequestContext ctx, HttpServletResponse response) throws Exception;
}
