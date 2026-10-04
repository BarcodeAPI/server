package org.barcodeapi.server.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;

/**
 * RequestContext.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026, Community Edition)
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

	public RequestContext(Request request) {
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

		// The original URI
		this.uri = request.getOriginalURI().toString();

		// Size of the request body
		this.body = request.getContentLength();
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
	 * Returns true if is an API request with no referer.
	 * 
	 * @return true if is an API request
	 */
	public boolean isAPIRequest() {
		return getSource().equals("API");
	}

	/**
	 * Returns true if is an App based request
	 * 
	 * @return true if is an App based request
	 */
	public boolean isAppRequest() {
		return !isAPIRequest();
	}
}