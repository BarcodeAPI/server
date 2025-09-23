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
public class TestLimiterHandler extends ServerTestBase {

	@Test
	public void testServer_TestLimiterEndpoint() {

		setHeader("Authorization", "Token=appTest");
		serverGet("/limiter/");

		Assert.assertEquals("Response Code", //
				HttpStatus.OK_200, getResponseCode());

		Assert.assertEquals("Response Header", //
				"application/json;charset=utf-8", getHeader("Content-Type"));

		try {

			String response = LibExtrasStreams.readStream(getResponse());
			JSONObject parsed = new JSONObject(response);

			// Caller is set
			Assert.assertEquals("Caller", //
					"AppTest", parsed.getString("caller"));

			// Creation time of limiter
			Assert.assertTrue("Created", //
					parsed.getLong("created") <= System.currentTimeMillis());

			// Expiration time of limiter
			Assert.assertTrue("Expiration time", //
					parsed.getLong("expires") > System.currentTimeMillis());

			// Last touched time
			Assert.assertTrue("Last Touched", //
					parsed.getLong("last") <= System.currentTimeMillis());

			// Limiter enforcement
			Assert.assertTrue("Limiter Enforcement", //
					parsed.getBoolean("enforce") == true);

			// Token limit is set
			Assert.assertTrue("Token Limit", //
					parsed.getDouble("tokenLimit") == 1000);

			// Token count is set
			Assert.assertTrue("Token Count", //
					parsed.getDouble("tokenCount") >= 100);

			// Token spend is set
			Assert.assertTrue("Token Spend", //
					parsed.getDouble("tokenSpend") >= 0);

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
}
