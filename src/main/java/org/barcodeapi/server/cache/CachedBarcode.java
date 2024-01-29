package org.barcodeapi.server.cache;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CachedObject;
import org.barcodeapi.server.core.CodeType;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

public class CachedBarcode extends CachedObject {

	private static final JSONObject conf = AppConfig.get()//
			.getJSONObject("cache").getJSONObject("barcode");

	private final CodeType type;
	private final String raw;
	private final byte[] data;

	private final String nice;
	private final String encoded;
	private final String checksum;

	public CachedBarcode(CodeType type, String raw, byte[] data) {
		this.setTimeout(conf.getInt("life"), TimeUnit.MINUTES);

		this.type = type;
		this.raw = raw;

		this.data = data;
		this.checksum = LibExtrasHashes.sumMD5(data);

		this.nice = CodeUtils.stripIllegal(raw);
		this.encoded = CodeUtils.encode(raw);
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
