package org.barcodeapi.test.cust;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestAmazon.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestAmazon extends ServerTestBase {

	@Test
	public void testAmazon_Product_B00() {

		apiGet("B00XXXXXXX");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"B00XXXXXXX", getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testAmazon_Product_X00() {

		apiGet("X00XXXXXXX");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"X00XXXXXXX", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testAmazon_Internal_Pod() {

		apiGet("000001234");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"CODABAR", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"000001234", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testAmazon_Internal_Bin() {

		apiGet("P-4-059G945");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"P-4-059G945", getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testAmazon_Internal_Cart() {

		apiGet("CART_00xxxxxx_S");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"CART_00xxxxxx_S", getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testAmazon_Internal_Gaylord() {

		apiGet("GAYLORD_A5Rvkj_Z");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"GAYLORD_A5Rvkj_Z", getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testAmazon_Internal_TBA000() {

		apiGet("TBA012345678909");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"TBA012345678909", getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testAmazon_Internal_SBxx() {

		apiGet("SxXxXxXxXx_001_v");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"SxXxXxXxXx_001_v", getHeader("X-Barcode-Content"));
	}
}
