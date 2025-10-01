package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestServerRoot.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestServerRoot extends ServerTestBase {

	@Test
	public void testServer_RootRedirect() {

		serverGet("/");

		Assert.assertEquals("Response Code", //
				HttpStatus.FOUND_302, getResponseCode());

		Assert.assertTrue("Redirect", //
				getHeader("Location").endsWith("/index.html"));
	}

	@Test
	public void testServer_StaticRoot() {

		serverGet("/index.html");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// TODO Assert loaded response body
	}

	@Test
	public void testServer_StaticResource() {

		serverGet("/ext/logo.svg");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// TODO Assert loaded image
	}

	@Test
	public void testServer_302RedirectToAPI() {

		serverGet("/unknown.html");

		Assert.assertEquals("Response Code", //
				HttpStatus.FOUND_302, getResponseCode());

		Assert.assertTrue("Redirect", //
				getHeader("Location").endsWith("/api/auto/unknown.html"));
	}
}
