package org.barcodeapi.server.cache;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.core.utils.StringUtils;
import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.gen.CodeType;

public class CachedBarcode extends CachedObject {

	private final CodeType type;
	private final String raw;
	private final byte[] data;

	private final String nice;
	private final String encoded;
	private final String checksum;

	public CachedBarcode(CodeType type, String raw, byte[] data) {
		this.setTimeout(3, TimeUnit.DAYS);

		this.type = type;
		this.raw = raw;

		this.data = data;
		this.checksum = CodeUtils.getMD5Sum(data);

		this.nice = StringUtils.stripIllegal(raw);
		this.encoded = StringUtils.encode(raw);
	}

	public CodeType getType() {

		return type;
	}

	public String getRaw() {

		return raw;
	}

	public byte[] getData() {

		this.touch();
		return data;
	}

	public int getDataSize() {

		return data == null ? 0 : data.length;
	}

	public String getChecksum() {

		return checksum;
	}

	public String getNice() {

		return nice;
	}

	public String getEncoded() {

		return encoded;
	}
}
