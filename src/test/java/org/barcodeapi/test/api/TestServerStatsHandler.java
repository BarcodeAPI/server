package org.barcodeapi.test.api;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestServerStatsHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestServerStatsHandler extends ServerTestBase {

	@Test
	public void testServer_TestStatsEndpoind() {

		serverGet("/server/stats/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}
}
