package org.barcodeapi.test.core;

import org.barcodeapi.core.utils.AuthUtils;
import org.barcodeapi.server.ServerTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestAuthUtils.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestAuthUtils extends ServerTestBase {

	@Test
	public void TestAuthUtils_TestPassGen() {

		String testUserHash = AuthUtils.passHash("test");
		String testUserAuth = AuthUtils.formatUser("testUser", testUserHash);

		Assert.assertEquals("AuthString", //
				"testUser:9F86D081884C7D659A2FEAA0C55AD015A3BF4F1B2B0B822CD15D6C15B0F00A08", testUserAuth);
	}
}
