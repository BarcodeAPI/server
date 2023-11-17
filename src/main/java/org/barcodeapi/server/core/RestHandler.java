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

		// build the request context
		RequestContext ctx = new RequestContext(baseRequest);

		// hit the counters
		getStats().hitCounter("request", "count");
		getStats().hitCounter("request", "method", ctx.getMethod());
		getStats().hitCounter("request", "target", _NAME, "count");
		getStats().hitCounter("request", "target", _NAME, "method", ctx.getMethod());

		// skip if already handled
		if (!baseRequest.isHandled()) {
			baseRequest.setHandled(true);
		} else {
			return;
		}

		// log the request
		LibLog.clogF("request", //
				((ctx.getProxy() == null) ? "I4001" : "I4002"), //
				_NAME, target, ctx.getSource(), ctx.getIP(), ctx.getProxy());

		// setup default response headers
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Server", "BarcodeAPI.org");
		response.setHeader("Server-Node", serverName);
		response.setHeader("Accept-Charset", "utf-8");
		response.addCookie(ctx.getSession().getCookie());

		// add CORS headers
		addCORSHeaders(baseRequest, response);

		// authenticate the user if required
		if (apiAuthRequired && (!ctx.isAdmin())) {

			getStats().hitCounter("request", "authfail");
			getStats().hitCounter("request", "target", _NAME, "authfail");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("WWW-Authenticate", "Basic realm=BarcodeAPI.org Admin API");
			return;
		}

		// done if only options request
		if (ctx.getMethod().equals("OPTIONS")) {
			return;
		}

		try {

			// check if allowed by rate limiter
			if (apiRateLimited && !ctx.getLimiter().allowRequest()) {
				getStats().hitCounter("request", "limited");
				getStats().hitCounter("request", "target", _NAME, "limited");
				LibLog._clogF("E0609", ctx.getLimiter().getCaller());
				response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
				response.setHeader("X-ClientRateLimited", "YES");
				return;
			}
		} finally {

			// allow user to see their token count
			response.setHeader("X-RateLimit-Tokens", //
					String.format("%.2f", ctx.getLimiter().numTokens()));
		}

		try {

			// call the implemented method
			this.onRequest(ctx, response);
		} catch (Exception e) {

			// TODO handle this
			e.printStackTrace();
		} finally {

			// calculate execution time
			long runTime = System.currentTimeMillis() - ctx.getTimestamp();

			// hit the counters
			getStats().hitCounter(runTime, "request", "time");
			getStats().hitCounter(runTime, "request", "target", _NAME, "time");
		}
	}

	protected abstract void onRequest(RequestContext ctx, HttpServletResponse response) throws Exception;

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
}
