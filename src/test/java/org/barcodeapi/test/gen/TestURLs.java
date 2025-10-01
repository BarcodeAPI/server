package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestURLs.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestURLs extends ServerTestBase {

	@Test
	public void testURL_TestHTTP() {

		apiGet("http://barcodeapi.org/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("http://barcodeapi.org/"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testURL_TestHTTPS() {

		apiGet("https://barcodeapi.org/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("https://barcodeapi.org/"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testURL_TestBarcodeOptions() {

		apiGet("https://barcodeapi.org/", "size=20");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("https://barcodeapi.org/"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testURL_TestURLOptions() {

		apiGet("https://barcodeapi.org/", "product=123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("https://barcodeapi.org/?product=123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testURL_TestURLEncoded() {

		apiGet("https://barcodeapi.org/?product=123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("https://barcodeapi.org/?product=123"), getHeader("X-Barcode-Content"));
	}
}
