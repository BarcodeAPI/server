package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestServerHeaders extends ServerTestBase {

	@Test
	public void testServerHeaders_testServerNameHeader() {

		apiGet("test-headers");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Server Name Header", //
				"BarcodeAPI.org", getHeader("Server"));
	}

	@Test
	public void testServerHeaders_testServerNodeHeader() {

		apiGet("test-headers");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertNotNull("Server Node Header", getHeader("Server-Node"));
	}

	@Test
	public void testServerHeaders_testServerCacheHeaders() {

		apiGet("test-headers");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Access Control Max Age Header", //
				"86400", getHeader("Access-Control-Max-Age"));

		Assert.assertEquals("Access Control Allow Header", //
				"true", getHeader("Access-Control-Allow-Credentials"));

		Assert.assertEquals("Access Control Origin Header", //
				"*", getHeader("Access-Control-Allow-Origin"));
	}

	@Test
	public void testServerHeaders_testServerTokens() {

		apiGet("test-headers");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertNotNull("RateLimit Token Header", getHeader("X-RateLimit-Tokens"));
	}
}
