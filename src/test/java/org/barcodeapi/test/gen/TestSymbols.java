package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TestSymbols.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestSymbols extends ServerTestBase {

	@Test
	public void testSymbols_TestExclamation() {

		apiGet("test!123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test!123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestQuestion() {

		apiGet("test?123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test?123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestDollarSign() {

		apiGet("test$123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test$123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestForwardSlash() {

		apiGet("test/123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"test%2F123", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestBackSlash() {

		apiGet("test\123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"test\123", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestPoundSign() {

		apiGet("test#123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test#123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestParentheses() {

		apiGet("test()123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test()123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestColon() {

		apiGet("test:123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test:123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestSemicolon() {

		apiGet("test;123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test;123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestAtSign() {

		apiGet("test@123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test@123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestCarrot() {

		apiGet("test^123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test^123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestSingleQuote() {

		apiGet("test'123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test'123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestDoubleQuote() {

		apiGet("test\"123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test\"123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestSqureBrackets() {

		apiGet("test[|]123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test[|]123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestCurlyBrackets() {

		apiGet("test{}123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test{}123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestGtLtEq() {

		apiGet("test<=>123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test<=>123"), getHeader("X-Barcode-Content"));
	}

	@Test
	@Ignore
	public void testSymbols_TestPercent() {

		apiGet("test%123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test%123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestAmpersand() {

		apiGet("test&123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test&123"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testSymbols_TestUnicode() {

		apiGet("testΩ123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("testΩ123"), getHeader("X-Barcode-Content"));
	}
}
