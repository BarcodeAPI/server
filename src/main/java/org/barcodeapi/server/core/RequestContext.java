package org.barcodeapi.server.core;

import org.barcodeapi.server.limits.CachedLimiter;
import org.barcodeapi.server.limits.LimiterCache;
import org.barcodeapi.server.session.CachedSession;
import org.eclipse.jetty.server.Request;

public class RequestContext {

	private final Request request;

	private final long ts;

	private final String ip;
	private final String fwd;

	private final String method;

	private final String uri;

	private final String source;

	private final CachedLimiter limiter;
	private final CachedSession session;

	private final boolean admin;

	public RequestContext(Request request) {
		this.request = request;

		// request time
		this.ts = System.currentTimeMillis();

		// update scheme is via proxy
		String proto = request.getHeader("X-Forwarded-Proto");
		if (proto != null) {
			request.getMetaData().getURI().setScheme(proto);
		}

		// get origin IP address / proxy
		String ip = request.getRemoteAddr();
		String fwd = request.getHeader("X-Forwarded-For");

		// swap with header if via proxy
		this.fwd = (fwd == null) ? null : ip;
		this.ip = (fwd == null) ? ip : fwd;

		// the request method
		this.method = request.getMethod();

		// get the request URI
		this.uri = request.getOriginalURI();

		// get source of the request
		String ref = request.getHeader("Referer");
		this.source = (ref != null) ? ref : "API";

		// get limiter by API key or IP
		String key = request.getParameter("key");
		this.limiter = (key != null) ? //
				LimiterCache.getByKey(key) : LimiterCache.getByIp(ip);

		// get user session info
		this.session = SessionHelper.getSession(request);
		session.hit(request.getOriginalURI().toString());

		// check if user logged in
		this.admin = SessionHelper.validateAdmin(request);
	}

	public Request getRequest() {
		return this.request;
	}

	public long getTimestamp() {
		return this.ts;
	}

	public String getProxy() {
		return this.fwd;
	}

	public String getIP() {
		return this.ip;
	}

	public String getMethod() {
		return this.method;
	}

	public String getUri() {
		return this.uri;
	}

	public String getSource() {
		return this.source;
	}

	public CachedLimiter getLimiter() {
		return this.limiter;
	}

	public CachedSession getSession() {
		return this.session;
	}

	public boolean isAdmin() {
		return this.admin;
	}
}
