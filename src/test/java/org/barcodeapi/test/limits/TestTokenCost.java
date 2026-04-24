package org.barcodeapi.test.limits;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.gen.BarcodeRequest;
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
			{ "qr", "test", "2.1" }, //
			{ "128", "test", "1.51" }, //
			// { "39", "TEST", "0.96" }, //
			{ "dm", "https://www.google.com", "2.55" } //
	};

	@Test
	public void testTokenCost_Runner() {

		for (int index = 0; index < testCodes.length; index++) {

			String type = testCodes[index][0];
			String data = testCodes[index][1];
			String cost = testCodes[index][2];

			apiGet(type, data, null);

			Assert.assertEquals("Response Code", //
					HttpStatus.OK_200, getResponseCode());

			Assert.assertEquals("RateLimit Cost", //
					cost, getHeader("X-RateLimit-Cost"));
		}
	}
}
