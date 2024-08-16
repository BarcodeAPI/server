package org.barcodeapi.server.core;

import org.barcodeapi.server.cache.CachedBarcode;
import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.gen.BarcodeRequest;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * GenerationException.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class GenerationException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum ExceptionType {
		EMPTY(400, "/128/$$@INVALID$$@"),

		INVALID(400, "/128/$$@INVALID$$@"),

		LIMITED(402, "/128/$$@RATE$$@$$@LIMIT$$@"),

		BLACKLIST(403, "/128/$$@BLACKLIST$$@"),

		CHECKSUM(409, "/128/$$@CHECKSUM$$@"),

		FAILED(500, "/128/$$@FAILED$$@"),

		BUSY(503, "/128/$$@BUSY$$@");

		private final int statusCode;

		private final CachedBarcode barcodeImage;

		ExceptionType(int statusCode, String req) {
			this.statusCode = statusCode;
			try {
				this.barcodeImage = BarcodeGenerator//
						.requestBarcode(BarcodeRequest.fromURI(req));
			} catch (GenerationException e) {
				throw LibLog._clog("E0789", e)//
						.asException(IllegalStateException.class);
			}
		}

		public int getStatusCode() {
			return statusCode;
		}

		public CachedBarcode getBarcodeImage() {
			return barcodeImage;
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
