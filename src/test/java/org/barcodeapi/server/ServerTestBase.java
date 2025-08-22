package org.barcodeapi.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.barcodeapi.core.ServerLauncher;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class ServerTestBase {

	private static final String SERVER_HOST = "127.0.0.1";
	private static final String SERVER_PORT = "8899";

	protected static URI serverUri;

	protected static ServerLauncher apiServer;

	private Map<String, String> urlHeaders;

	private HttpURLConnection urlConnection;

	private int responseCode;

	private BufferedReader response;

	@BeforeClass
	public static void startServer() {

		HttpURLConnection.setFollowRedirects(false);

		try {

			apiServer = new ServerLauncher(new String[] { "--port", SERVER_PORT });
			apiServer.launch();

			serverUri = new URI(String.format("http://%s:%s", SERVER_HOST, SERVER_PORT));

		} catch (Exception e) {

			Assert.fail("Failed to initialize server.");
		}
	}

	@Before
	public void setupHeaders() {

		urlHeaders = new HashMap<>();
	}

	protected String encode(String data) {

		try {

			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception e) {

			return null;
		}
	}

	protected Map<String, String> headers() {
		return urlHeaders;
	}

	protected void apiGet(String path) {
		apiGet(path, null);
	}

	protected void apiGet(String path, String args) {

		try {

			String encoded = URLEncoder.encode(path, "UTF-8");
			String request = "/api/" + encoded;
			if (args != null) {
				request += ('?' + args);
			}
			serverGet(request);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace(System.err);
			Assert.fail("IOException.");
		}
	}

	protected void serverGet(String path) {

		try {

			URL url = serverUri.resolve(path).toURL();
			urlConnection = (HttpURLConnection) url.openConnection();

			// Add test request headers
			for (Map.Entry<String, String> entry : urlHeaders.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}

			// Connect and get response
			urlConnection.connect();
			responseCode = urlConnection.getResponseCode();

			if (responseCode == HttpStatus.OK_200) {
				response = new BufferedReader(//
						new InputStreamReader(urlConnection.getInputStream()));
			}
		} catch (Exception e) {

			e.printStackTrace(System.err);
			Assert.fail("IOException.");
		}
	}

	protected int getResponseCode() {

		return responseCode;
	}

	protected String getHeader(String header) {

		return urlConnection.getHeaderField(header);
	}

	protected BufferedReader getResponse() {

		return response;
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
