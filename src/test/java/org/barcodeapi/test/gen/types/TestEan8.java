package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestEan8 extends ServerTestBase {

	@Test
	public void testEan8_7Characters() throws Exception {

		apiGet("/8/1234567");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"1234567", getHeader("X-CodeData"));
	}

	@Test
	public void testEan8_8Characters() throws Exception {

		apiGet("/8/12345670");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"12345670", getHeader("X-CodeData"));
	}

	@Test
	public void testEan8_8CharactersInvalidChecksum() throws Exception {

		apiGet("/8/12345678");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed to render [ 12345678 ]", getResponse().readLine());
	}

	@Test
	public void testEan8_TooShort() throws Exception {

		apiGet("/8/123456");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed to render [ 123456 ]", getResponse().readLine());
	}

	@Test
	public void testEan8_TooLong() throws Exception {

		apiGet("/8/123456789");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed to render [ 123456789 ]", getResponse().readLine());
	}

	@Test
	public void testEan8_WithLetters() throws Exception {

		apiGet("/8/ABCDEFGH");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed to render [ ABCDEFGH ]", getResponse().readLine());
	}
}
