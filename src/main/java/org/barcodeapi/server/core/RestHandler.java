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

	public boolean apiAuthRequired() {
		return apiAuthRequired;
	}

	public boolean apiRateLimited() {
		return apiRateLimited;
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

		// authenticate the user if required
		if (apiAuthRequired && (!ctx.isAdmin())) {

			getStats().hitCounter("request", "authfail");
			getStats().hitCounter("request", "target", _NAME, "authfail");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("WWW-Authenticate", "Basic realm=BarcodeAPI.org Admin API");
			return;
		}

		// add open CORS headers
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", //
				(ctx.getOrigin() != null) ? ctx.getOrigin() : "*");

		// request complete if only options
		if (ctx.getMethod().equals("OPTIONS")) {
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
			return;
		}

		// send token count to user
		ctx.getLimiter().touch();
		response.setHeader("X-RateLimit-Tokens", //
				String.format("%.2f", ctx.getLimiter().numTokens()));

		try {

			// call the implemented method
			this.onRequest(ctx, response);
		} catch (Exception | Error e) {

			// log the error
			LibLog._clog("E0699", e);
		} finally {

			// calculate total processing time
			long runTime = System.currentTimeMillis() - ctx.getTimestamp();

			// hit the time and status counters
			getStats().hitCounter(runTime, "request", "time");
			getStats().hitCounter(runTime, "request", "target", _NAME, "time");
			getStats().hitCounter("request", "result", ("_" + response.getStatus()));
			getStats().hitCounter("request", "target", _NAME, "result", ("_" + response.getStatus()));
		}
	}

	protected abstract void onRequest(RequestContext ctx, HttpServletResponse response) throws Exception;
}
