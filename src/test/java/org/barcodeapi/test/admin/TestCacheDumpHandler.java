package org.barcodeapi.test.admin;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestCacheDumpHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestCacheDumpHandler extends ServerTestBase {

	@Test
	public void TestCacheDumpHandler_Unauthenticated() {

		serverGet("/admin/cache/dump/");

		Assert.assertEquals("Response Code", //
				HttpStatus.UNAUTHORIZED_401, getResponseCode());
	}
}
