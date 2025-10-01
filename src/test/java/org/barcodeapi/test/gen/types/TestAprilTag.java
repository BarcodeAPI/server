package org.barcodeapi.test.gen.types;

import org.barcodeapi.server.ServerTestBase;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.junit.Assert;
import org.junit.Test;

public class TestAprilTag extends ServerTestBase {

	@Test
	public void testAprilTag_UnsupportedFamily() {

		apiGet("april/tagWoah:1");

		Assert.assertEquals("Response Code", //
				ExceptionType.INVALID.getStatusCode(), getResponseCode());
	}
}
