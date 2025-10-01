package org.barcodeapi.test.gen;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestSpecial.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestSpecial extends ServerTestBase {

	@Test
	public void testSpecial_Price() {

		apiGet("$12.34");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Code Type", //
				"Code39", getHeader("X-Barcode-Type"));

		Assert.assertEquals("Code Data", //
				encode("$12.34"), getHeader("X-Barcode-Content"));
	}
}
