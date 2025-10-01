package org.barcodeapi.test.api;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TestServerTypesHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestSessionHandler extends ServerTestBase {

	@Test
	@Ignore
	public void testServer_TestGetSessionEndpoint() {

		serverGet("/session/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Response Header", //
				"application/json;charset=utf-8", getHeader("Content-Type"));

		JSONObject response = getResponseAsJSON();

		// Key is set
		Assert.assertEquals("Session Key", //
				36, response.getString("key").length());

		// Created time of session
		Assert.assertTrue("Creation Time", //
				response.getLong("created") <= System.currentTimeMillis());

		// Expiration time of session
		Assert.assertTrue("Expiration Time", //
				response.getLong("expires") > System.currentTimeMillis());

		// Last touched time
		Assert.assertTrue("Last Touched", //
				response.getLong("last") <= System.currentTimeMillis());

		// Total hit count for session
		Assert.assertTrue("Hit Count", //
				response.getLong("count") >= 1);

		// Addresses list []

		// Request list []

	}

	@Test
	@Ignore
	public void testServer_TestDeleteSessionEndpoint() {

		serverGet("/session/");
		JSONObject response = getResponseAsJSON();
		String key = response.getString("key");

		setHeader("session", key);
		serverDelete("/session/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());
	}
}
