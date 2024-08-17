package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestEan8 extends ServerTestBase {

	@Test
	public void testEan8_7Nums() {

		apiGet("8/1234567");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"1234567", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testEan8_8Nums() {

		apiGet("8/12345670");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"12345670", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testEan8_8NumsInvalidChecksum() throws Exception {

		apiGet("8/12345678");

		Assert.assertEquals("Response Code", //
				HttpStatus.CONFLICT_409, getResponseCode());
	}

	@Test
	public void testEan8_TooShort() throws Exception {

		apiGet("8/123456");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}

	@Test
	public void testEan8_TooLong() throws Exception {

		apiGet("8/123456789");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}

	@Test
	public void testEan8_WithLetters() throws Exception {

		apiGet("8/ABCDEFGH");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}

	@Test
	public void testEan8_WithSymbols() throws Exception {

		apiGet("8/!@");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}

	@Test
	@Ignore
	public void testEan8_WithUnicode() throws Exception {

		apiGet("8/Ω");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}
}
