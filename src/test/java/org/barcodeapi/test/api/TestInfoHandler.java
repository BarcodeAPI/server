package org.barcodeapi.test.api;

import java.io.IOException;

import org.barcodeapi.server.ServerTestBase;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.mclarkdev.tools.libextras.LibExtrasStreams;

/**
 * TestServerTypesHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public class TestInfoHandler extends ServerTestBase {

	@Test
	public void testServer_TestInfoEndpoint() {

		serverGet("/info/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Response Header", //
				"application/json;charset=utf-8", getHeader("Content-Type"));

		try {
			
			String response = LibExtrasStreams.readStream(getResponse());
			JSONObject parsed = new JSONObject(response);

			// Server Uptime is set
			Assert.assertTrue("Server Uptime", //
					parsed.getLong("uptime") > 0);

			// Server Hostname is set
			Assert.assertTrue("Server Hostname", //
					parsed.getString("hostname").length() > 0);

			// Server version is set
			Assert.assertTrue("Server Version", //
					parsed.getInt("version") >= 0);

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
}
