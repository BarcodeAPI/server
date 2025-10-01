package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestBlacklist.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestBlacklist extends ServerTestBase {

	@Test
	public void testBlacklist_testBlacklistItem() {

		apiGet("_tstblk_");

		Assert.assertEquals("Response Code", //
				GenerationException.ExceptionType.BLACKLIST.getStatusCode(), getResponseCode());
	}
}
