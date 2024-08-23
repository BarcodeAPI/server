package org.barcodeapi.server.core;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.gen.BarcodeGenerator;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * GenerationException.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class GenerationException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum ExceptionType {
		EMPTY(400, "/128/$$@EMPTY$$@"),

		INVALID(400, "/128/$$@INVALID$$@"),

		BLACKLIST(403, "/128/$$@BLACKLIST$$@"),

		FORMAT(406, "/128/$$@FORMAT$$@"),

		CHECKSUM(409, "/128/$$@CHECKSUM$$@"),

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

	public GenerationException(ExceptionType type) {
		this(type, new Throwable(type.toString()));
	}

	public GenerationException(ExceptionType type, Throwable throwable) {
		super(throwable);

		this.type = type;
	}

	public ExceptionType getExceptionType() {

		return type;
	}
}
