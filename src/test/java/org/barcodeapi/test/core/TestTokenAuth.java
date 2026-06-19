package org.barcodeapi.test.core;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestTokenAuth.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestTokenAuth extends ServerTestBase {

	@Test
	public void TestTokenAuth_TokenAuth() {

		final String APIKEY = "NOLIMITS";

		// Give the server our API key
		setHeader("Authorization", ("token=" + APIKEY));

		// Make the request
		serverGet("/api/dm/9990");

		// We got an okay response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// Verify correct user account
		Assert.assertEquals("RateLimit Caller", //
				"AppTest (Unlimited)", getHeader("X-RateLimit-Caller"));

		// Verify barcode content
		Assert.assertEquals("Code Data", //
				encode("9990"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void TestTokenAuth_GetParam() {

		final String APIKEY = "NOLIMITS";

		// Make the request
		serverGet("/api/dm/9992" + ("?token=" + APIKEY));

		// We got an okay response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// Verify correct user account
		Assert.assertEquals("RateLimit Caller", //
				"AppTest (Unlimited)", getHeader("X-RateLimit-Caller"));

		// Verify barcode content
		Assert.assertEquals("Code Data", //
				encode("9992"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void TestTokenAuth_GetParamWithOptions() {

		final String APIKEY = "NOLIMITS";

		// Make the request
		serverGet("/api/dm/9994" + ("?token=" + APIKEY) + "&dpi=150");

		// We got an okay response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// Verify correct user account
		Assert.assertEquals("RateLimit Caller", //
				"AppTest (Unlimited)", getHeader("X-RateLimit-Caller"));

		// Verify barcode content
		Assert.assertEquals("Code Data", //
				encode("9994"), getHeader("X-Barcode-Content"));
	}
}
