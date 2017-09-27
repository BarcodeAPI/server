package org.barcodeapi.server.gen;

import org.barcodeapi.server.cache.ImageCache;
import org.barcodeapi.server.core.CodeType;

public abstract class CodeGenerator {

	private final CodeType codeType;

	public CodeGenerator(CodeType type) {

		this.codeType = type;

		ImageCache.getInstance().createCache(type);
	}

	public CodeType getType() {

		return codeType;
	}

	public abstract byte[] generateCode(String data);
}
