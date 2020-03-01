package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestServerRoot extends ServerTestBase {

	@Test
	public void testServer_RootRedirect() {

		serverGet("/");

		Assert.assertEquals("Response Code", //
				HttpStatus.FOUND_302, getResponseCode());

		// TODO Assert redirect to /index.html
	}

	@Test
	public void testServer_StaticRoot() {

		serverGet("/index.html");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// TODO Assert loaded response body
	}

	@Test
	public void testServer_StaticImage() {

		serverGet("/logo.png");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		// TODO Assert loaded image
	}

	@Test
	public void testServer_404RedirectToAPI() {

		serverGet("/unknown.html");

		Assert.assertEquals("Response Code", //
				HttpStatus.FOUND_302, getResponseCode());

		// TODO Assert redirect to /api/auto/unknown.html
	}
}
