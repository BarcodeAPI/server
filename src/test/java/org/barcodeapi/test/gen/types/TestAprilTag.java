package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

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
