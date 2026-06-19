package org.barcodeapi.test.gen.types;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestQRCode.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestQRCode extends ServerTestBase {

	@Test
	public void testQRCode_CorrectionL() {

		apiGet("qr", "test", "correction=l");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testQRCode_CorrectionM() {

		apiGet("qr", "test", "correction=m");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testQRCode_CorrectionQ() {

		apiGet("qr", "test", "correction=q");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testQRCode_CorrectionH() {

		apiGet("qr", "test", "correction=h");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}
}
