package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.test.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestAprilTag.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026)
 */
public class TestAprilTag extends ServerTestBase {
	
	@Test
	public void testAprilTag_Empty() {

		apiGet("april", "");

		Assert.assertEquals("Response Code", //
				HttpStatus.NOT_ACCEPTABLE_406, getResponseCode());
	}


	@Test
	public void testAprilTag_UnsupportedFamily() {

		apiGet("april", "tagWoah:1");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());
	}
}
