package org.barcodeapi.server.cache;

public class CachedObject {

	private final long timeCreated;

	private final byte[] barcodeData;

	public CachedObject(byte[] data) {

		this.timeCreated = System.currentTimeMillis();

		this.barcodeData = data;
	}

	public long getTimeCreated() {

		return timeCreated;
	}

	public byte[] getData() {

		return barcodeData;
	}

	public long getDataSize() {

		return barcodeData.length;
	}
}
