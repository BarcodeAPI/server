package org.barcodeapi.test.gen.types;

import java.net.HttpURLConnection;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestEan8 extends ServerTestBase {

	@Test
	public void testEan8_7Characters() throws Exception {

		HttpURLConnection http = apiGet("/8/1234567");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, http.getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", http.getHeaderField("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"1234567", http.getHeaderField("X-CodeData"));
	}

	@Test
	public void testEan8_8Characters() throws Exception {

		HttpURLConnection http = apiGet("/8/12345670");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, http.getResponseCode());

		Assert.assertEquals("Code Type", //
				"EAN8", http.getHeaderField("X-CodeType"));

		Assert.assertEquals("Code Data", //
				"12345670", http.getHeaderField("X-CodeData"));
	}

	@Test
	public void testEan8_8CharactersInvalidChecksum() throws Exception {

		HttpURLConnection http = apiGet("/8/12345678");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, http.getResponseCode());
	}
}
