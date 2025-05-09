package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestOutputFormat extends ServerTestBase {

	@Test
	public void testOutputFormat_default() {

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("$12.34"), getHeader("X-Barcode-Content"));
	}
	
	// Img
	// b64
	// json
	// invalid
	// force download
	
}
