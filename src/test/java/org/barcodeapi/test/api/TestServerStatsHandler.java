package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestServerStatsHandler extends ServerTestBase {

	@Test
	public void testServer_TestStatsEndpoind() {

		serverGet("/server/stats/");

		Assert.assertEquals("Response Code", //
				HttpStatus.UNAUTHORIZED_401, getResponseCode());
	}
}
