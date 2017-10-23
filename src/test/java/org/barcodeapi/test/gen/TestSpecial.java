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
				"Code39", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				encode("$12.34"), getHeader("X-CodeData"));
	}

	@Test
	public void testSpecial_testAmazonASIN() {

		apiGet("BXXXXXXXXX");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"BXXXXXXXXX", getHeader("X-CodeData"));
	}

	@Test
	public void testSpecial_testAmazonPod() {

		apiGet("000001234");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"CODABAR", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"000001234", getHeader("X-CodeData"));
	}

	@Test
	public void testSpecial_testAmazonBin() {

		apiGet("P-4-059G945");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"P-4-059G945", getHeader("X-CodeData"));
	}
}
