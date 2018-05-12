package org.barcodeapi.server.cache;

import java.util.Properties;

import org.barcodeapi.core.utils.CodeUtils;

public class CachedObject {

	private final long timeCreated;

	private final byte[] barcodeData;

	private final String checksum;

	private long lastAccess = 0;

	private Properties properties;

	public CachedObject(byte[] data) {

		this.timeCreated = System.currentTimeMillis();

		this.barcodeData = data;

		this.checksum = CodeUtils.getMD5Sum(data);

		this.properties = new Properties();
	}

	public long getTimeCreated() {

		return timeCreated;
	}

	public byte[] getData() {

		lastAccess = System.currentTimeMillis();
		return barcodeData;
	}

	public long getDataSize() {

		return barcodeData.length;
	}

	public String getChecksum() {

		return checksum;
	}

	public long getLastAccess() {

		return lastAccess;
	}

	public Properties getProperties() {

		return properties;
	}
}
