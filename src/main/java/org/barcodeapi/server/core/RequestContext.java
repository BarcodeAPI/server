package org.barcodeapi.server.core;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedSession;
import org.barcodeapi.server.cache.LimiterCache;
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

		// Request time
		this.ts = System.currentTimeMillis();

		// Update scheme is via proxy
		String proto = request.getHeader("X-Forwarded-Proto");
		if (proto != null) {
			request.getMetaData().getURI().setScheme(proto);
		}

		// Get origin IP address / proxy
		String ip = request.getRemoteAddr();
		String fwd = request.getHeader("X-Forwarded-For");

		// Swap with header if via proxy
		this.fwd = (fwd == null) ? null : ip;
		this.ip = (fwd == null) ? ip : fwd;

		// The request method
		this.method = request.getMethod();

		// Get the request URI
		this.uri = request.getOriginalURI();

		// Get the origin
		this.origin = request.getHeader("origin");

		// Get source of the request
		String ref = request.getHeader("Referer");
		this.source = (ref != null) ? ref : "API";

		// Check if user logged in
		this.admin = SessionHelper.validateAdmin(request);

		// Get limiter by API key or IP
		CachedLimiter limiter = null;
		String apiKey = request.getHeader("Authorization");
		if (apiKey != null) {
			limiter = LimiterCache.getByKey(apiKey);
		}
		if (limiter == null) {
			limiter = LimiterCache.getByIp(ip);
		}
		this.limiter = limiter;

		// Get user session info
		CachedSession tmpSession = SessionHelper.getSession(request);
		this.session = (tmpSession != null) ? tmpSession : //
				((createSession) ? SessionHelper.createSession() : null);

		// Hit the session
		if (this.session != null) {
			session.hit(request.getOriginalURI().toString());
		}
	}

	/**
	 * Returns the raw request.
	 * 
	 * @return the raw request
	 */
	public Request getRequest() {
		return this.request;
	}

	/**
	 * Returns the time the request was initiated.
	 * 
	 * @return time request initiated
	 */
	public long getTimestamp() {
		return this.ts;
	}

	/**
	 * Returns the proxy for the request.
	 * 
	 * @return the proxy for request
	 */
	public String getProxy() {
		return this.fwd;
	}

	/**
	 * Returns the IP for the request.
	 * 
	 * @return the IP for the request
	 */
	public String getIP() {
		return this.ip;
	}

	/**
	 * Returns the method for the request.
	 * 
	 * @return the method for the request
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * Returns the URI for the request.
	 * 
	 * @return the URI for the request
	 */
	public String getUri() {
		return this.uri;
	}

	/**
	 * Returns the origin for the request.
	 * 
	 * @return the origin for the request
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * Returns the source for the request.
	 * 
	 * @return the source for the request
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Returns true if user is Admin.
	 * 
	 * @return if user is admin
	 */
	public boolean isAdmin() {
		return this.admin;
	}

	/**
	 * Returns the limiter for the request.
	 * 
	 * @return the limiter for the request
	 */
	public CachedLimiter getLimiter() {
		return this.limiter;
	}

	/**
	 * Returns true if the request has a session.
	 * 
	 * @return if the request has a session
	 */
	public boolean hasSession() {
		return (this.session != null);
	}

	/**
	 * Returns the session for the request.
	 * 
	 * @return the session for the request
	 */
	public CachedSession getSession() {
		return this.session;
	}
}
