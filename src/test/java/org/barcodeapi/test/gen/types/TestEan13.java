package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestEan13 extends ServerTestBase {

	@Test
	public void testEan13_12Nums() {

		apiGet("13/123456789012");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"123456789012", getHeader("X-CodeData"));
	}

	@Test
	public void testEan13_13Nums() {

		apiGet("13/1234567890128");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"1234567890128", getHeader("X-CodeData"));
	}

	@Test
	public void testEan13_8NumsInvalidChecksum() throws Exception {

		apiGet("13/1234567890123");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed [ EAN13 ] with [ 1234567890123 ] reason [ Expected checksum : 8 ]", //
				getResponse().readLine());
	}

	@Test
	public void testEan13_TooShort() throws Exception {

		apiGet("13/12345678901");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed [ EAN13 ] with [ 12345678901 ] reason [ Invalid data for selected code type ]", //
				getResponse().readLine());
	}

	@Test
	public void testEan13_TooLong() throws Exception {

		apiGet("13/12345678901234");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed [ EAN13 ] with [ 12345678901234 ] reason [ Invalid data for selected code type ]", //
				getResponse().readLine());
	}

	@Test
	public void testEan13_WithLetters() throws Exception {

		apiGet("13/123456789O123");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed [ EAN13 ] with [ 123456789O123 ] reason [ Invalid data for selected code type ]", //
				getResponse().readLine());
	}

	@Test
	public void testEan8_WithSymbols() throws Exception {

		apiGet("13/!@");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed [ EAN13 ] with [ !@ ] reason [ Invalid data for selected code type ]", //
				getResponse().readLine());
	}

	@Test
	public void testEan8_WithUnicode() throws Exception {

		apiGet("13/Ω");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());

		Assert.assertEquals("Error Message", //
				"Failed [ EAN13 ] with [ ? ] reason [ Invalid data for selected code type ]", //
				getResponse().readLine());
	}
}
