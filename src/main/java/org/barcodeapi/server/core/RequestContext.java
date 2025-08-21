package org.barcodeapi.server.core;

import java.util.ArrayList;
import java.util.List;

import org.barcodeapi.server.cache.CachedLimiter;
import org.barcodeapi.server.cache.CachedSession;
import org.barcodeapi.server.cache.LimiterCache;
import org.barcodeapi.server.cache.Subscriber;
import org.barcodeapi.server.cache.SubscriberCache;
import org.eclipse.jetty.server.Request;

/**
 * RequestContext.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
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

	private final String method;

	private final String uri;

	private final int body;

	private final String origin;

	private final String source;

	private final Format[] formats;

	private final String admin;

	private final Subscriber subscriber;

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

		// Size of the request body
		this.body = request.getContentLength();

		// Get the origin
		this.origin = request.getHeader("origin");

		// Get source of the request
		String ref = request.getHeader("Referer");
		this.source = (ref != null) ? ref : "API";

		// Determine the associated subscriber by IP
		Subscriber user = null;

		String admin = null;
		String authStr = request.getHeader("Authorization");

		if (authStr != null) {
			if (authStr.startsWith("Basic")) {
				authStr = authStr.substring(6);
				admin = SessionHelper.validateUser(authStr);
			} else //
			if (authStr.startsWith("Token")) {
				authStr = authStr.substring(6);
				user = SubscriberCache.getByKey(authStr);
			}
		}

		// Lookup user based on IP
		if(user == null) {
			user = SubscriberCache.getByIP(ip);
		}

		// User ID based on customer association or IP
		String userID = (user != null) ? user.getCustomer() : ip;
		
		this.admin = admin;
		this.subscriber = user;
		this.limiter = LimiterCache.getLimiter(user, userID);

		// Get user session info
		CachedSession tmpSession = SessionHelper.getSession(request);
		this.session = (tmpSession != null) ? tmpSession : //
				((createSession) ? SessionHelper.createSession() : null);

		// Hit the session
		if (this.session != null) {
			session.hit(ip, request.getOriginalURI().toString());
		}

		// Determine output format and encoding
		this.formats = Format.parse(request.getHeader("Accept"));
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
	 * Returns the source for the request.
	 * 
	 * @return the source for the request
	 */
	public String getSource() {
		return this.source;
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
