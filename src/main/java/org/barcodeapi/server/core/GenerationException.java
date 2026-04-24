package org.barcodeapi.server.core;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.gen.BarcodeGenerator;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * GenerationException.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class GenerationException extends Exception {

	private static final long serialVersionUID = 20241123L;

	public enum ExceptionType {

		INVALID(400, "/128/$$@INVALID$$@"),

		ABUSE(403, "/128/$$@ABUSE$$@"),

		EMPTY(406, "/128/$$@EMPTY$$@"),

		CHECKSUM(409, "/128/$$@CHECKSUM$$@"),

		BLACKLIST(412, "/128/$$@BLACKLIST$$@"),

		LIMITED(429, "/128/$$@RATE$$@$$@LIMIT$$@"),

		FAILED(500, "/128/$$@FAILED$$@"),

		BUSY(503, "/128/$$@BUSY$$@");

		private final int status;

		private final CachedBarcode image;

		ExceptionType(int status, String target) {
			this.status = status;
			try {
				this.image = BarcodeGenerator.requestBarcode(target);
			} catch (GenerationException e) {
				throw LibLog._clog("E0789", e)//
						.asException(IllegalStateException.class);
			}
		}

		public int getStatusCode() {
			return status;
		}

		public CachedBarcode getBarcodeImage() {
			return image;
		}
	}

	private final ExceptionType type;

	public GenerationException(ExceptionType type, Throwable throwable) {
		super(throwable.getCause() != null ? throwable.getCause() : throwable);

		this.type = type;
	}

	public ExceptionType getExceptionType() {

		return type;
	}
}
