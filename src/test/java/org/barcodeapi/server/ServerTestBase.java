package org.barcodeapi.server;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.core.ServerLauncher;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import com.mclarkdev.tools.libextras.LibExtrasStreams;

/**
 * ServerTestBase.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2025)
 */
public abstract class ServerTestBase {

	private static final String SERVER_HOST = "127.0.0.1";
	private static final String SERVER_PORT = "8899";

	protected static URI serverUri;

	protected static ServerLauncher apiServer;

	private Map<String, String> urlHeaders;

	private HttpURLConnection urlConnection;

	private int responseCode;

	private InputStream response;

	@BeforeClass
	public static void startServer() {

		HttpURLConnection.setFollowRedirects(false);

		try {

			apiServer = new ServerLauncher(//
					new String[] { "--port", SERVER_PORT, "--config", "apptest" });
			apiServer.launch();

			serverUri = new URI(String.format("http://%s:%s", SERVER_HOST, SERVER_PORT));

		} catch (Exception | Error e) {

			Assert.fail("Failed to initialize server.");
		}
	}

	@Before
	public void setupHeaders() {

		urlHeaders = new HashMap<>();
		
		// Preconditions, set request header
		setHeader("Accept", "*/*");
	}

	protected void setHeader(String key, String val) {

		urlHeaders.put(key, val);
	}

	protected String encode(String data) {

		try {

			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception e) {

			e.printStackTrace(System.err);
			Assert.fail("Failed encoding data.");
			return null;
		}
	}

	protected void apiGet(String path) {
		apiGet(path, null);
	}

	protected void apiGet(String path, String args) {

		String request = "/api/" + encode(path);
		if (args != null) {
			request += ('?' + args);
		}

		serverGet(request);
	}

	protected void serverGet(String path) {

		request("GET", path);
	}

	protected void serverPost(String path) {

		request("POST", path);
	}

	protected void serverDelete(String path) {

		request("DELETE", path);
	}

	protected void request(String method, String path) {

		try {

			// Build the URL and open the connection
			URL url = serverUri.resolve(path).toURL();
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(method);

			// Add request headers for the test
			for (Map.Entry<String, String> entry : urlHeaders.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}

			// Connect and get response
			urlConnection.connect();
			responseCode = urlConnection.getResponseCode();

			// Check for 200 response code
			if (responseCode == HttpStatus.OK_200) {
				response = urlConnection.getInputStream();
			}

		} catch (Exception | Error e) {

			// Print the exception and fail
			e.printStackTrace(System.err);
			Assert.fail("Exception in request.");
		}
	}

	protected int getResponseCode() {

		return responseCode;
	}

	protected String getHeader(String header) {

		return urlConnection.getHeaderField(header);
	}

	protected InputStream getResponse() {

		return response;
	}

	protected JSONObject getResponseAsJSON() {

		try {

			return new JSONObject(//
					LibExtrasStreams.readStream(getResponse()));

		} catch (Exception | Error e) {

			e.printStackTrace(System.err);
			Assert.fail("Exception processing response.");
			return null;
		}
	}

	@AfterClass
	public static void stopServer() {

		try {

			apiServer.stop();
		} catch (Exception e) {

			Assert.fail("Failed to stop server.");
		}
	}
}
