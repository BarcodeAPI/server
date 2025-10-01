package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestUPCA extends ServerTestBase {

	@Test
	public void testUPCA_12Nums() {

		apiGet("a/123456789012");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_A", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"123456789012", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testUPCA_11Nums() {

		apiGet("a/12345678901");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_A", getHeader("X-Barcode-Type"));

		// Checksum is filled in
		Assert.assertEquals("Code Data", //
				"123456789012", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testUPCA_8NumsInvalidChecksum() throws Exception {

		apiGet("a/123456789013");

		Assert.assertEquals("Response Code", //
				ExceptionType.CHECKSUM.getStatusCode(), getResponseCode());

		Assert.assertEquals("Error Message", //
				"Invalid checksum: expected 2", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCA_TooShort() throws Exception {

		apiGet("a/1234567890");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCA_TooLong() throws Exception {

		apiGet("a/1234567890123");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCA_WithLetters() throws Exception {

		apiGet("a/ABCDEFGH");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCA_WithSymbols() throws Exception {

		apiGet("a/!@");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCA_WithUnicode() throws Exception {

		apiGet("a/Ω");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}
}
