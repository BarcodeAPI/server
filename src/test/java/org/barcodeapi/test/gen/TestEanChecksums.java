package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestEanChecksums extends ServerTestBase {

	@Test
	public void testEanChecksums_Ean8Calculated() {

		apiGet("8/1234567");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("1234567"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testEanChecksums_Ean8Valid() {

		apiGet("8/12345670");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("12345670"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testEanChecksums_Ean8Invalid() {

		apiGet("8/12345679");

		Assert.assertEquals("Response Code", //
				HttpStatus.CONFLICT_409, getResponseCode());
	}

	@Test
	public void testEanChecksums_Ean13Calculated() {

		apiGet("13/123456789012");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("123456789012"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testEanChecksums_Ean13Valid() {

		apiGet("13/1234567890128");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("1234567890128"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testEanChecksums_Ean13Invalid() {

		apiGet("13/1234567890129");

		Assert.assertEquals("Response Code", //
				HttpStatus.CONFLICT_409, getResponseCode());
	}
}
