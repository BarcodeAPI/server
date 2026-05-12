package org.barcodeapi.test.gen.types;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestAprilTag.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestQRCode extends ServerTestBase {

	@Test
	public void testQRCode_CorrectionL() {

		apiGet("qr", "test", "correction=L");

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testQRCode_CorrectionM() {

		apiGet("qr", "test", "correction=M");

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testQRCode_CorrectionQ() {

		apiGet("qr", "test", "correction=Q");

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testQRCode_CorrectionH() {

		apiGet("qr", "test", "correction=H");

		// We got a rate limited response code
		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}
}
