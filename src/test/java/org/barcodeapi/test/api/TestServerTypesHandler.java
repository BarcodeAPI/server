package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestServerTypesHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestServerTypesHandler extends ServerTestBase {

	@Test
	public void testServer_TestTypesEndpoint() {

		serverGet("/types/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}
}
