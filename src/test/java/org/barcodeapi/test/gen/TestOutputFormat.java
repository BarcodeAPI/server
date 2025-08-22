package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

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

		headers().put("Accept", "application/json");
		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"application/json;charset=utf-8", getHeader("Content-Type"));
	}

	@Test
	public void testOutputFormat_HTML() {

		headers().put("Accept", "text/html");
		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"text/html;charset=utf-8", getHeader("Content-Type"));
	}

	@Test
	public void testOutputFormat_PNG() {

		headers().put("Accept", "image/png");
		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Format", //
				"image/png;charset=utf-8", getHeader("Content-Type"));
	}
}
