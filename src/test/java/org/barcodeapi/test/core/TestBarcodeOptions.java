package org.barcodeapi.test.core;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestBarcodeOptions.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestBarcodeOptions extends ServerTestBase {

	@Test
	public void testBarcodeRequest_TestStripDefaults() {

		try {
			BarcodeRequest req = //
					BarcodeRequest.fromURI("/api/auto/abc?fg=000000&bg=ffffff");

			Assert.assertFalse("Request is not Simple", req.isComplex());
		} catch (GenerationException e) {

			Assert.fail("Failed to create request.");
		}
	}

	@Test
	public void testBarcodeOptions_Options() {

		try {
			BarcodeRequest r = BarcodeRequest//
					.fromURI("/api/128/barcode_test?bg=fffff0&fg=00000f");

			Assert.assertEquals("Request Type", //
					"Code128", r.getType().getName());

			Assert.assertEquals("Request Data", //
					"barcode_test", r.getData());

			Assert.assertEquals("Request Options", //
					2, r.getOptions().length());

			Assert.assertEquals("Request Options (FG)", //
					"00000f", r.getOptions().getString("fg"));

			Assert.assertEquals("Request Options (BG)", //
					"fffff0", r.getOptions().getString("bg"));

		} catch (GenerationException e) {

			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBarcodeOptions_MissingOption() {

		apiGet("128/RD309874", "dpi=100&&size=20");

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
	public void testBarcodeOptions_DoubleOption() {

		apiGet("128/RD309874", "dpi=100&dpi=120");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code128", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				"RD309874", getHeader("X-Barcode-Content"));

		Assert.assertEquals("Code Format", //
				"image/png;charset=utf-8", getHeader("Content-Type"));
	}
}
