package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestServerTypesHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestPlansHandler extends ServerTestBase {

	@Test
	public void testServer_TestPlansEndpoint() {

		serverGet("/plans/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Response Header", //
				"application/json;charset=utf-8", getHeader("Content-Type"));

		JSONObject response = getResponseAsJSON();

		JSONObject free = response.getJSONObject("free");

		Assert.assertEquals("Free Plan Limit", //
				1000, free.getInt("limit"));

		Assert.assertEquals("Free Plan Enforcement", //
				true, free.getBoolean("enforce"));

		JSONArray paid = response.getJSONArray("paid");

		Assert.assertNotEquals("Paid Plan Info", 0, paid.length());
	}
}
