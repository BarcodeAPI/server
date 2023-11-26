package org.barcodeapi.server.core;

public class GenerationException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum ExceptionType {
		EMPTY, BLACKLIST, FAILED, INVALID, LIMITED, BUSY;
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
