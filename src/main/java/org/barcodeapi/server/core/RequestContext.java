package org.barcodeapi.server.core;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedSession;
import org.barcodeapi.server.limits.LimiterCache;
import org.eclipse.jetty.server.Request;

/**
 * RequestContext.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class RequestContext {

	private final Request request;

	private final long ts;

	private final String ip;
	private final String fwd;

	private final String method;

	private final String uri;

	private final String origin;

	private final String source;

	private final boolean admin;

	private final CachedLimiter limiter;
	private final CachedSession session;

	public RequestContext(Request request, boolean createSession) {
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

		// get the origin
		this.origin = request.getHeader("origin");

		// get source of the request
		String ref = request.getHeader("Referer");
		this.source = (ref != null) ? ref : "API";

		// check if user logged in
		this.admin = SessionHelper.validateAdmin(request);

		// get limiter by API key or IP
		CachedLimiter limiter = null;
		String apiKey = request.getParameter("key");
		if (apiKey != null) {
			limiter = LimiterCache.getByKey(apiKey);
		}
		if (limiter == null) {
			limiter = LimiterCache.getByIp(ip);
		}
		this.limiter = limiter;

		// get user session info
		CachedSession tmpSession = SessionHelper.getSession(request);
		this.session = (tmpSession != null) ? tmpSession : //
				((createSession) ? SessionHelper.createSession() : null);

		if (this.session != null) {
			session.hit(request.getOriginalURI().toString());
		}
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

	public String getOrigin() {
		return this.origin;
	}

	public String getSource() {
		return this.source;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public CachedLimiter getLimiter() {
		return this.limiter;
	}

	public boolean hasSession() {
		return (this.session != null);
	}

	public CachedSession getSession() {
		return this.session;
	}
}
