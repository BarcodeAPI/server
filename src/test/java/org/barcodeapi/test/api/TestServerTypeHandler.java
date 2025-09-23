package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestServerTypeHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestServerTypeHandler extends ServerTestBase {

	@Test
	public void testServer_TestTypeEndpoind() {

		serverGet("/type/?type=a");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}

	@Test
	public void testServer_TestTypeEndpointNoArgs() {

		serverGet("/type/?");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}

	@Test
	public void testServer_TestTypeEndpointWrongTarget() {

		serverGet("/type/?type=abc");

		Assert.assertEquals("Response Code", //
				HttpStatus.BAD_REQUEST_400, getResponseCode());
	}
}
