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

			// Reputations is set and not 0
			Assert.assertTrue("Reputation", //
					parsed.getDouble("reputation") != 0);

			// Get time object
			JSONObject time = parsed.getJSONObject("time");

			// Creation time of limiter
			Assert.assertTrue("Created", //
					time.getLong("created") <= System.currentTimeMillis());

			// Expiration time of limiter
			Assert.assertTrue("Expiration time", //
					time.getLong("expires") > System.currentTimeMillis());

			// Last touched time
			Assert.assertTrue("Last Touched", //
					time.getLong("last") <= System.currentTimeMillis());

			// Get tokens object
			JSONObject tokens = parsed.getJSONObject("tokens");

			// Limiter enforcement
			Assert.assertTrue("Limiter Enforcement", //
					tokens.getBoolean("enforce") == true);

			// Token limit is set
			Assert.assertTrue("Token Limit", //
					tokens.getDouble("limit") == 1000);

			// Token count is set
			Assert.assertTrue("Token Count", //
					tokens.getDouble("count") >= 100);

			// Token spend is set
			Assert.assertTrue("Token Spend", //
					tokens.getDouble("spend") >= 0);

			// Token last mint time is set
			Assert.assertTrue("Last Mint Time", //
					tokens.getLong("minted") >= 0);

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
}
