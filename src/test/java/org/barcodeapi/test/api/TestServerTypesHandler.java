package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class TestServerTypesHandler extends ServerTestBase {

	@Test
	public void testServer_TestTypesEndpoint() {

		serverGet("/types/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}
}
