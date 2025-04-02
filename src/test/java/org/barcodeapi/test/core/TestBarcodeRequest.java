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
					GenerationException.ExceptionType.EMPTY, e.getExceptionType());
		}
	}

	@Test
	public void testBarcodeRequest_TesInvalidRequest() {

		try {
			BarcodeRequest.fromURI("/api/13/0123");

		} catch (GenerationException e) {

			Assert.assertEquals("Failure Reason", //
					GenerationException.ExceptionType.INVALID, e.getExceptionType());
		}
	}

	@Test
	public void testBarcodeRequest_TestBlacklistRequest() {

		try {
			BarcodeRequest.fromURI("/api/auto/_tstblk_");

		} catch (GenerationException e) {

			Assert.assertEquals("Failure Reason", //
					GenerationException.ExceptionType.BLACKLIST, e.getExceptionType());
		}
	}

	@Test
	public void testBarcodeRequest_TestCodeType_Auto() {

		try {
			BarcodeRequest r = BarcodeRequest//
					.fromURI("/api/auto/barcode_test");

			Assert.assertEquals("Request Type", //
					"Code128", r.getType().getName());

			Assert.assertEquals("Request Data", //
					"barcode_test", r.getData());

			Assert.assertEquals("Request Options", //
					0, r.getOptions().length());

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
					"Code39", r.getType().getName());

			Assert.assertEquals("Request Data", //
					"BARCODE", r.getData());

			Assert.assertEquals("Request Options", //
					0, r.getOptions().length());

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
					"Code128", r.getType().getName());

			Assert.assertEquals("Request Data", //
					"barcode_test", r.getData());

			Assert.assertEquals("Request Options", //
					0, r.getOptions().length());

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
					"Code128", r.getType().getName());

			Assert.assertEquals("Request Data", //
					"barcode_test", r.getData());

			Assert.assertEquals("Request Options", //
					2, r.getOptions().length());

			Assert.assertEquals("Request Options (FG)", //
					"000000", r.getOptions().getString("fg"));

			Assert.assertEquals("Request Options (BG)", //
					"ffffff", r.getOptions().getString("bg"));

		} catch (GenerationException e) {

			Assert.fail(e.getMessage());
		}
	}
}
