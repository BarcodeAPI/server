package org.barcodeapi.test.core;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.cache.ObjectCache;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestLimits extends ServerTestBase {

	@Test
	public void testLimits_50Tokens() {

		// Flush the cache
		ObjectCache.getCache(ObjectCache.CACHE_KEY).clearCache();

		// Request 50 Single-Token Barcodes
		for (int x = 0; x < 50; x++) {

			apiGet("8/00000000", "key=test00000050");

			Assert.assertEquals("Response Code: " + x, //
					HttpStatus.OK_200, getResponseCode());
		}

		// Final Request Fails
		apiGet("8/00000000", "key=test00000050");

		Assert.assertEquals("Response Code (Limited) ", //
				HttpStatus.PAYMENT_REQUIRED_402, getResponseCode());
	}
}
