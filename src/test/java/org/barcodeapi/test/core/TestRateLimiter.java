package org.barcodeapi.test.core;

import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestRateLimiter.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestRateLimiter extends ServerTestBase {

	private static final String FORMAT = "/api/qr/LIMIT-%04d";

	private String getURI(int id) {
		return String.format(FORMAT, id);
	}

	@Test
	public void testRateLimit_Unlimited() {

		final String APIKEY = "NOLIMITS";

		// Give the server our API key
		setHeader("Authorization", ("Token=" + APIKEY));

		// Make the request
		serverGet(getURI(9999));

		// Verify correct user account
		Assert.assertEquals("RateLimit Caller", //
				"AppTest (Unlimited)", getHeader("X-RateLimit-Caller"));

		// We got an okay response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// Proper token cost returned
		Assert.assertEquals("RateLimit Cost", //
				"2.25", getHeader("X-RateLimit-Cost"));

		// Header indicates unlimited
		Assert.assertEquals("RateLimit Tokens", //
				"-1.00", getHeader("X-RateLimit-Tokens"));
	}

	@Test
	public void testRateLimit_RateLimited() {

		final int TOKENS = 1000;
		final String APIKEY = "1000TOKENS";

		int expectedCount = 0;
		try {
			// Determine the cost per barcode
			BarcodeRequest bar = BarcodeRequest.fromURI(getURI(0));
			double requestCost = bar.getCost();

			// Determine the expected number of barcodes we can request
			expectedCount = (int) Math.floor((TOKENS / requestCost));
		} catch (GenerationException e) {
			Assert.fail();
		}

		// Give the server our API key
		setHeader("Authorization", ("Token=" + APIKEY));

		// Loop expected plus a few
		int requestCount = 0;
		for (int x = 0; x < (expectedCount + 50); x++) {

			// Wait until we get limited
			serverGet(getURI(x));
			if (getResponseCode() == 200) {
				requestCount++;
			} else {
				break;
			}
		}

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.TOO_MANY_REQUESTS_429, getResponseCode());

		// Verify correct user account
		Assert.assertEquals("RateLimit Caller", //
				"AppTest (1000)", getHeader("X-RateLimit-Caller"));

		// We got a rate limited response message
		Assert.assertEquals("Error Message", //
				"Client is rate limited, try again later. (u:AppTest (1000))", getHeader("X-Error-Message"));

		// We got the proper number of barcodes before hitting the limit
		Assert.assertEquals("Request Count", expectedCount, requestCount);
	}

	@Test
	public void testRateLimit_NoEnforce() {

		final int TOKENS = 25;
		final String APIKEY = "NOENFORCE";

		int expectedCount = 0;
		try {
			// Determine the cost per barcode
			BarcodeRequest bar = BarcodeRequest.fromURI(getURI(0));
			double requestCost = bar.getCost();

			// Determine the expected number of barcodes we can request
			expectedCount = (int) Math.floor((TOKENS / requestCost));
		} catch (GenerationException e) {
			Assert.fail();
		}

		// Give the server our API key
		setHeader("Authorization", ("Token=" + APIKEY));

		// Loop expected plus a few
		int requestCount = 0;
		expectedCount += 50;
		for (int x = 0; x < (expectedCount); x++) {

			// Wait until we get limited
			serverGet(getURI(x));
			if (getResponseCode() == 200) {
				requestCount++;
			} else {
				break;
			}
		}

		// We got an okay response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// Verify correct user account
		Assert.assertEquals("RateLimit Caller", //
				"AppTest (NoEnforce)", getHeader("X-RateLimit-Caller"));

		// We got the proper number of barcodes before hitting the limit
		Assert.assertEquals("Request Count", expectedCount, requestCount);

		// Client is out of tokens, but not limited
		Assert.assertTrue("RateLimit Tokens", //
				(Double.parseDouble(getHeader("X-RateLimit-Tokens")) < 5.0));
	}

	@Test
	public void testRateLimit_DisabledSubscriber() {

		// Give the server our API key
		final String APIKEY = "DISABLED";
		setHeader("Authorization", ("Token=" + APIKEY));

		// Make the request
		serverGet(getURI(0));

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// Verify default user account used
		Assert.assertEquals("RateLimit Caller", //
				"DevTest", getHeader("X-RateLimit-Caller"));
	}
}
