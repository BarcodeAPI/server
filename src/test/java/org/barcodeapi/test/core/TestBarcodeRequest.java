package org.barcodeapi.test.core;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.junit.Assert;
import org.junit.Test;

public class TestBarcodeRequest extends ServerTestBase {

	@Test
	public void testBarcodeRequest_TestEmptyRequest() {

		try {
			BarcodeRequest.fromURI("/api/auto/");

		} catch (GenerationException e) {

			Assert.assertEquals("Failure Reason", //
					e.getExceptionType(), GenerationException.ExceptionType.EMPTY);
		}
	}

	@Test
	public void testBarcodeRequest_TesInvalidRequest() {

		try {
			BarcodeRequest.fromURI("/api/13/0123");

		} catch (GenerationException e) {

			Assert.assertEquals("Failure Reason", //
					e.getExceptionType(), GenerationException.ExceptionType.INVALID);
		}
	}

	@Test
	public void testBarcodeRequest_TestBlacklistRequest() {

		try {
			BarcodeRequest.fromURI("/api/auto/_tstblk_");

		} catch (GenerationException e) {

			Assert.assertEquals("Failure Reason", //
					e.getExceptionType(), GenerationException.ExceptionType.BLACKLIST);
		}
	}
	
	@Test
	public void testBarcodeRequest_TestCodeType_Auto() {

		try {
			BarcodeRequest r = BarcodeRequest//
					.fromURI("/api/auto/barcode_test");

			Assert.assertEquals("Request Type", //
					r.getType().getName(), "Code128");

			Assert.assertEquals("Request Data", //
					r.getData(), "barcode_test");

			Assert.assertEquals("Request Options", //
					r.getOptions().length(), 0);

		} catch (GenerationException e) {

			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testBarcodeRequest_TestCodeType_Code39() {

		try {
			BarcodeRequest r = BarcodeRequest//
					.fromURI("/api/39/BARCODE");

			Assert.assertEquals("Request Type", //
					r.getType().getName(), "Code39");

			Assert.assertEquals("Request Data", //
					r.getData(), "BARCODE");

			Assert.assertEquals("Request Options", //
					r.getOptions().length(), 0);

		} catch (GenerationException e) {

			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBarcodeRequest_TestCodeType_Code128() {

		try {
			BarcodeRequest r = BarcodeRequest//
					.fromURI("/api/128/barcode_test");

			Assert.assertEquals("Request Type", //
					r.getType().getName(), "Code128");

			Assert.assertEquals("Request Data", //
					r.getData(), "barcode_test");

			Assert.assertEquals("Request Options", //
					r.getOptions().length(), 0);

		} catch (GenerationException e) {

			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBarcodeRequest_TestCodeType_WithOptions() {

		try {
			BarcodeRequest r = BarcodeRequest//
					.fromURI("/api/128/barcode_test?bg=ffffff&fg=000000");

			Assert.assertEquals("Request Type", //
					r.getType().getName(), "Code128");

			Assert.assertEquals("Request Data", //
					r.getData(), "barcode_test");

			Assert.assertEquals("Request Options", //
					r.getOptions().length(), 2);

			Assert.assertEquals("Request Options (FG)", //
					r.getOptions().getString("fg"), "000000");

			Assert.assertEquals("Request Options (BG)", //
					r.getOptions().getString("bg"), "ffffff");

		} catch (GenerationException e) {

			Assert.fail(e.getMessage());
		}
	}
}
