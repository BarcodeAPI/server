package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestUPCE extends ServerTestBase {

	@Test
	public void testUPCE_8Nums() {

		apiGet("e/01234565");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_E", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"01234565", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testUPCE_7Nums() {

		apiGet("e/0123456");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_E", getHeader("X-Barcode-Type"));

		// Checksum is filled in
		Assert.assertEquals("Code Data", //
				"01234565", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testUPCE_8NumsInvalidChecksum() throws Exception {

		apiGet("e/01234567");

		Assert.assertEquals("Response Code", //
				ExceptionType.CHECKSUM.getStatusCode(), getResponseCode());

		Assert.assertEquals("Error Message", //
				"Invalid checksum: expected 5", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCE_TooShort() throws Exception {

		apiGet("e/012345");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCE_TooLong() throws Exception {

		apiGet("e/012345678");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCE_WithLetters() throws Exception {

		apiGet("e/ABCDEFGH");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCE_WithSymbols() throws Exception {

		apiGet("e/!@");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}

	@Test
	public void testUPCE_WithUnicode() throws Exception {

		apiGet("e/Ω");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());

		Assert.assertEquals("Response Message", //
				"Invalid data for selected code type.", getHeader("X-Error-Message"));
	}
}
