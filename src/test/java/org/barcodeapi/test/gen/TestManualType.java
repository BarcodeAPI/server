package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestManualType.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestManualType extends ServerTestBase {
	
	@Test
	public void testManualType_AprilTag() {
		
		apiGet("april/tag36h10:0");
		
		Assert.assertEquals("Response Code",  //
				HttpStatus.OK_200, getResponseCode());
		
		Assert.assertEquals("Code Type", //
				"AprilTag", getHeader("X-Barcode-Type"));
		
		Assert.assertEquals("Code Data", //
				encode("tag36h10:0"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_Aztec() {

		apiGet("aztec/test");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Aztec", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_Codabar() {

		apiGet("codabar/000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"CODABAR", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("000000"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_UPC_E() {

		apiGet("e/00000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_E", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("00000000"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_UPC_A() {

		apiGet("a/000000000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"UPC_A", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("000000000000"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_Ean8() {

		apiGet("8/99999995");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("99999995"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_Ean13() {

		apiGet("13/0000000000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN13", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("0000000000000"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_Code39() {

		apiGet("39/TEST");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"TEST", getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_Code128() {

		apiGet("128/test");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_QRCode() {

		apiGet("qr/test");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"QRCode", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_DataMatrix() {

		apiGet("dm/test");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"DataMatrix", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_ITF14() {

		apiGet("14/00000000000000");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"ITF14", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("00000000000000"), getHeader("X-Barcode-Content"));
	}

	@Test
	public void testManualType_PDF417() {

		apiGet("417/test");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"PDF417", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test"), getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testManualType_RoyalMail() {

		apiGet("royal/11212345612345678");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"RoyalMail", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("11212345612345678"), getHeader("X-Barcode-Content"));
	}
	
	@Test
	public void testManualType_USPSMail() {

		apiGet("usps/0123456709498765432101234567891");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"USPSMail", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("0123456709498765432101234567891"), getHeader("X-Barcode-Content"));
	}
}
