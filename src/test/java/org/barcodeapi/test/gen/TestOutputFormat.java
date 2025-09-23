package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestOutputFormat.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestOutputFormat extends ServerTestBase {

	@Test
	public void testOutputFormat_default() {

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"image/png;charset=utf-8", getHeader("Content-Type"));
	}

	@Test
	public void testOutputFormat_JSON() {

		// Preconditions, set request header
		setHeader("Accept", "application/json");

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"application/json;charset=utf-8", getHeader("Content-Type"));
	}

	@Test
	public void testOutputFormat_HTML() {

		// Preconditions, set request header
		setHeader("Accept", "text/html");

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"text/html;charset=utf-8", getHeader("Content-Type"));
	}

	@Test
	public void testOutputFormat_PNG() {

		// Preconditions, set request header
		setHeader("Accept", "image/png");

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"image/png;charset=utf-8", getHeader("Content-Type"));
	}
}
