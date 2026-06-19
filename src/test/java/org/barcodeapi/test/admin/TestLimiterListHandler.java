package org.barcodeapi.test.admin;

import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestLimiterListHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestLimiterListHandler extends ServerTestBase {

	@Test
	public void TestLimiterListHandler_Unauthenticated() {

		serverGet("/admin/limiter/list/");

		Assert.assertEquals("Response Code", //
				HttpStatus.UNAUTHORIZED_401, getResponseCode());
	}
}
