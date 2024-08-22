package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestSpecial extends ServerTestBase {

	@Test
	public void testSpecial_testPrice() {

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("$12.34"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSpecial_testURL_http() {

		apiGet("http://barcodeapi.org/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("http://barcodeapi.org/"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSpecial_testURL_https() {

		apiGet("https://barcodeapi.org/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("https://barcodeapi.org/"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSpecial_testAmazonASIN() {

		apiGet("BXXXXXXXXX");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"BXXXXXXXXX", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSpecial_testAmazonPod() {

		apiGet("000001234");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"CODABAR", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"000001234", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSpecial_testAmazonBin() {

		apiGet("P-4-059G945");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"P-4-059G945", getHeader("X-Barcode-Content"));
	}
}
