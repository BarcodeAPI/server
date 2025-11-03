package org.barcodeapi.test.cust;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestMiscCustomer.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestMiscCustomer extends ServerTestBase {
	
	@Test
	public void testMiscCustomer_CustomEAN13() {

		apiGet("ean13/8859178779797", "width=2&height=20&format=png");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"8859178779797", getHeader("X-Barcode-Content"));

		Assert.assertEquals("Code Format", //
				"image/png;charset=utf-8", getHeader("Content-Type"));
	}
	
	@Test
	public void testMiscCustomer_CustomCode128() {

		apiGet("128/RD309874", "dpi=100&rotation=0&color=%23000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"RD309874", getHeader("X-Barcode-Content"));

		Assert.assertEquals("Code Format", //
				"image/png;charset=utf-8", getHeader("Content-Type"));
	}
	
	@Test
	public void testMiscCustomer_MACAddress() {
		
		apiGet("aa:11:bb:22:cc:33");
	}
}
