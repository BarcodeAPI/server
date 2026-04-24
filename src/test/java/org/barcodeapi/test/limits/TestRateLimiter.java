package org.barcodeapi.test.limits;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.gen.BarcodeRequest;
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

		// Give the server out API key
		setHeader("Authorization", ("Token=" + APIKEY));

		serverGet(getURI(9999));

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("RateLimit Caller", //
				"AppTest (Unlimited)", getHeader("X-RateLimit-Caller"));

		Assert.assertEquals("RateLimit Cost", //
				"2.25", getHeader("X-RateLimit-Cost"));

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

		// Give the server out API key
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

		// We got the proper number of barcodes before hitting the limit
		Assert.assertEquals("Request Count", expectedCount, requestCount);

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.TOO_MANY_REQUESTS_429, getResponseCode());

		// We got a rate limited response message
		Assert.assertEquals("Error Message", //
				"Client is rate limited, try again later.", getHeader("X-Error-Message"));
	}

}
