package org.barcodeapi.test.core;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestTokenCost.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestTokenCost extends ServerTestBase {

	private static final String[][] testCodes = { //
			{ "auto", "Try Me!", "0.0" }, //
			{ "auto", "Test-123", "2.2" }, //
			{ "auto", "Hello, world!", "2.325" }, //
			{ "auto", "https://barcodeapi.org/", "2.575" }, //

			{ "april", "123", "3.0" }, //
			{ "aztec", "TEST-AZTEC", "2.5" }, //
			{ "codabar", "01234567", "1.05" }, //
			{ "39", "TEST-39", "1.08" }, //
			{ "93", "TEST-93", "1.08" }, //
			{ "128", "TEST-128", "1.77" }, //
			{ "dm", "TEST-DM", "2.175" }, //
			{ "dm", "https://barcodeapi.org/", "2.575" }, //
			{ "8", "00000000", "1.0" }, //
			{ "13", "0000000000000", "1.0" }, //
			{ "14", "00000000000000", "1.0" }, //
			{ "417", "TEST-PDF417", "2.775" }, //
			{ "qr", "TEST-QR", "2.175" }, //
			{ "qr", "https://barcodeapi.org/", "2.575" }, //
			{ "royal", "0000000000", "1.5" }, //
			{ "a", "000000000000", "1.0" }, //
			{ "e", "00000000", "1.0" }//
	};

	@Test
	public void testTokenCost_Runner() {

		for (int index = 0; index < testCodes.length; index++) {

			String type = testCodes[index][0];
			String data = testCodes[index][1];
			String cost = testCodes[index][2];

			apiGet(type, data, null);

			Assert.assertEquals(String.format(//
					"Response Code (%s/%s)", type, data), //
					HttpStatus.OK_200, getResponseCode());

			Assert.assertEquals(String.format(//
					"RateLimit Cost (%s/%s)", type, data), //
					cost, getHeader("X-RateLimit-Cost"));
		}
	}
}
