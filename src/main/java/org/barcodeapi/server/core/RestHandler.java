package org.barcodeapi.server.core;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.MultipartConfigElement;
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
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public abstract class RestHandler extends AbstractHandler {

	private final String _NAME;

	private final String serverName;

	private final LibMetrics stats;

	private boolean enableMultipart = false;

	public RestHandler() {
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
	}

	public String name() {
		return _NAME;
	}

	public String server() {
		return serverName;
	}

	public LibMetrics getStats() {
		return stats;
	}

	public void enableMultipart(boolean enable) {
		this.enableMultipart = enable;
	}

	public boolean multipartEnabled() {
		return enableMultipart;
	}

	public void _impl(String target, Request baseRequest, HttpServletRequest request, //
			HttpServletResponse response) throws IOException, ServletException {
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, //
			HttpServletResponse response) throws IOException, ServletException {

		// Build the request context
		RequestContext ctx = new RequestContext(baseRequest);

		// Hit the counters
		getStats().hitCounter("request", "count");
		getStats().hitCounter("request", "method", ctx.getMethod());
		getStats().hitCounter("request", "secure", (ctx.isSecure() ? "yes" : "no"));
		getStats().hitCounter("request", "target", _NAME, "count");
		getStats().hitCounter("request", "target", _NAME, "method", ctx.getMethod());

		// Skip if already handled
		if (baseRequest.isHandled()) {
			return;
		}

		// Set as handled by us
		baseRequest.setHandled(true);

		// Log the request (replace line endings)
		String targetLog = target.replaceAll("\n", "---");
		LibLog.clogF("request", //
				((ctx.getProxy() == null) ? "I4001" : "I4002"), //
				_NAME, targetLog, ctx.getSource(), ctx.getIP(), ctx.getProxy());

		// Setup default response headers
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Server", "BarcodeAPI.org");
		response.setHeader("Server-Node", serverName);
		response.setHeader("Accept-Charset", "utf-8");

		// Add open CORS headers
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", //
				(ctx.getOrigin() != null) ? ctx.getOrigin() : "*");

		// Request complete if only options
		if (ctx.getMethod().equals("OPTIONS")) {
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
			return;
		}

		// Setup accept multi-part
		if (enableMultipart) {
			baseRequest.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement("./"));
			if (!baseRequest.getContentType().startsWith("multipart/")) {
				return;
			}
		}

		try {

			// Run raw implementation overrides
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

	/**
	 * The implemented logic to run on execution of the handler.
	 * 
	 * @param ctx      the request context
	 * @param response the server response
	 * @throws Exception processing failure
	 */
	protected abstract void onRequest(RequestContext ctx, HttpServletResponse response) throws Exception;
}
