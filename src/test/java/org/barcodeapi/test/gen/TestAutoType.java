package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestAutoType extends ServerTestBase {

	@Test
	public void testAutoType_UPC_E() {

		apiGet("00000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_E", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"00000000", getHeader("X-CodeData"));
	}
	
	@Test
	public void testAutoType_UPC_A() {

		apiGet("000000000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_A", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"000000000000", getHeader("X-CodeData"));
	}
	
	@Test
	public void testAutoType_Ean8() {

		apiGet("99999999");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"99999999", getHeader("X-CodeData"));
	}

	@Test
	public void testAutoType_Ean13() {

		apiGet("9999999999999");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"9999999999999", getHeader("X-CodeData"));
	}

	@Test
	public void testAutoType_Code39() {

		apiGet("ABC123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"ABC123", getHeader("X-CodeData"));
	}

	@Test
	public void testAutoType_Code128() {

		apiGet("abc123");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"abc123", getHeader("X-CodeData"));
	}

	@Test
	public void testAutoType_QRCode() {

		apiGet("$♠1A");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"$?1A", getHeader("X-CodeData"));
	}

	@Test
	public void testAutoType_DataMatrix() {

		// Build 64 char request
		String req = "";
		for (int x = 0; x <= 64; x++) {
			req += "0";
		}

		apiGet(req);

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"DataMatrix", getHeader("X-CodeType"));

		Assert.assertEquals("Code Data", //
				req, getHeader("X-CodeData"));
	}
}
