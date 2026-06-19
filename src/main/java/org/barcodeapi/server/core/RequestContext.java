package org.barcodeapi.server.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedSession;
import org.barcodeapi.server.cache.LimiterCache;
import org.barcodeapi.server.cache.Subscriber;
import org.barcodeapi.server.cache.SubscriberCache;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;

/**
 * RequestContext.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class RequestContext {

	public enum Format {

		ANY("*/*", null),

		TEXT("text/plain", ".txt"),

		HTML("text/html", ".html"),

		PNG("image/png", ".png"),

		JSON("application/json", ".json");

		private final String mime;

		private final String ext;

		Format(String mime, String ext) {
			this.mime = mime;
			this.ext = ext;
		}

		public String getMime() {
			return mime;
		}

		public String getExt() {
			return ext;
		}

		public static Format[] parse(String accept) {
			if (accept == null) {
				return (new Format[] { Format.ANY });
			}

			// Loop each supported format
			List<Format> supported = new ArrayList<>();
			for (Format f : Format.values()) {

				// Check if MIME type matches
				if (accept.contains(f.getMime())) {
					supported.add(f);
				}
			}

			// Return as static array
			return supported.toArray(//
					new Format[supported.size()]);
		}
	}

	private final Request request;

	private final long ts;

	private final String ip;
	private final String fwd;

	private final boolean secure;

	private final String method;

	private final String uri;

	private final Format[] formats;

	private final int body;

	private final String origin;

	private final String source;

	private final String admin;

	private final Subscriber subscriber;

	private final CachedLimiter limiter;
	private final CachedSession session;

	public RequestContext(Request request, boolean createSession) {
		this.request = request;

		// Request time
		this.ts = System.currentTimeMillis();

		// Get origin IP address / proxy
		String ip = request.getRemoteAddr();
		String fwd = request.getHeader("X-Forwarded-For");

		// Swap with header if via proxy
		this.fwd = (fwd == null) ? null : ip;
		this.ip = ip = (fwd == null) ? ip : fwd;

		// Update scheme if via proxy, determine if secure
		HttpURI rawUri = request.getMetaData().getURI();
		String proto = request.getHeader("X-Forwarded-Proto");
		rawUri.setScheme(proto != null ? proto : "http");
		this.secure = rawUri.getScheme().equals("https");

		// Request method and origin
		this.method = request.getMethod();
		this.origin = request.getHeader("Origin");

		// Determine the source of the request
		String ref = request.getHeader("Referer");
		this.source = (ref != null) ? ref : "API";

		// Determine output format and encoding
		this.formats = Format.parse(//
				request.getHeader("Accept"));

		// Check for request token
		String authStr = request.getHeader("Authorization");
		String reqUri = request.getOriginalURI().toString();
		int tokenAt = reqUri.indexOf("token=");
		if (tokenAt > 0) {

			// Extract the token from the URI
			int tokenEnd = reqUri.indexOf('&', tokenAt);
			tokenEnd = (tokenEnd > 0) ? tokenEnd : reqUri.length();
			authStr = reqUri.substring(tokenAt, tokenEnd);

			// Rebuild the URI with the token extracted
			reqUri = (reqUri.substring(0, tokenAt)) + //
					((tokenEnd == reqUri.length()) ? "" : //
							(reqUri.substring(tokenEnd, (reqUri.length() - 1))));
		}

		// The requested method and URI
		this.uri = reqUri;

		// Size of the request body
		this.body = request.getContentLength();

		// Lookup API key
		String admin = null;
		Subscriber user = null;
		if (authStr != null) {

			String authType = authStr.substring(0, 5);
			String authData = authStr.substring(6);

			switch (authType) {
			case "Basic":
				admin = SessionHelper.validateUser(authData);
				break;

			case "Token":
			case "token":
				user = SubscriberCache.getByKey(authData);
				break;
			}
		}

		// Lookup user based on application
		if (user == null && ref != null) {
			try {
				user = SubscriberCache.getByApp(//
						(new URI(ref)).getHost());
			} catch (URISyntaxException ignored) {
			}
		}

		// Lookup user based on IP
		if (user == null) {
			user = SubscriberCache.getByIP(ip);
		}

		// Assign resolved user info to the context
		this.admin = admin;
		this.subscriber = user;

		// Lookup or create new user session context
		CachedSession userSession = SessionHelper.getSession(request);
		this.session = (userSession != null) ? userSession : //
				((createSession) ? SessionHelper.createSession() : null);

		// Hit the session if it exists
		if (this.session != null) {
			session.hit(ip, this.uri);
		}

		// Get and touch the limiter
		this.limiter = LimiterCache.getLimiter(user, ip);
		this.limiter.touch(this.session);
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
	 * Returns true if the request is secure to the client.
	 * 
	 * @return the request is secure to the client
	 */
	public boolean isSecure() {
		return this.secure;
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
	 * Returns the requested output format.
	 * 
	 * @return the requested output format
	 */
	public Format[] getFormats() {
		return this.formats;
	}

	/**
	 * Returns if the request has additional content.
	 * 
	 * @return if the request has additional content
	 */
	public boolean hasBody() {

		return (this.body > 0);
	}

	/**
	 * Returns the length of the additional content.
	 * 
	 * @return the length of the additional content
	 */
	public int getBodySize() {

		return this.body;
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
	 * Returns the source of the request.
	 * 
	 * @return the source of the request
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Returns the user currently logged in.
	 * 
	 * @return the user currently logged in
	 */
	public String getAdmin() {
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
	 * Returns the subscriber of the request.
	 * 
	 * @return the subscriber of the request
	 */
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	/**
	 * Returns the session for the request.
	 * 
	 * @return the session for the request
	 */
	public CachedSession getSession() {
		return this.session;
	}

	/**
	 * Returns true if the session was newly created.
	 * 
	 * @return if the session was newly created
	 */
	public boolean hasNewSession() {
		return (this.session != null) && //
				(this.session.getAccessCount() == 1);
	}
}
