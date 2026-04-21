package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestForceDownload.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestForceDownload extends ServerTestBase {

	@Test
	public void testForceDownload_ContentDisposition() {

		setHeader("X-ForceDownload", "yes");
		apiGet("128", "test_download");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("test_download"), getHeader("X-Barcode-Content"));

		Assert.assertTrue("Forced Download", //
				getHeader("Content-Disposition").startsWith("attachment; "));
	}
}
