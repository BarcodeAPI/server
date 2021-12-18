package org.barcodeapi.server.cache;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CachedObject;

public class CachedBarcode extends CachedObject {

	private final byte[] cachedData;
	private final String checksum;
	private Properties properties;

	public CachedBarcode(byte[] data) {
		this.setTimeout(1, TimeUnit.HOURS);

		this.cachedData = data;
		this.checksum = CodeUtils.getMD5Sum(data);
		this.properties = new Properties();
	}

	public byte[] getData() {

		this.touch();
		return cachedData;
	}

	public int getDataSize() {

		return cachedData == null ? 0 : cachedData.length;
	}

	public String getChecksum() {

		return checksum;
	}

	public Properties getProperties() {

		return properties;
	}
}
