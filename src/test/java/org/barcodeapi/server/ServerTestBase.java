package org.barcodeapi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.barcodeapi.core.ServerLoader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public abstract class ServerTestBase {

	private static final String SERVER_HOST = "127.0.0.1";
	private static final String SERVER_PORT = "8899";

	protected static URI serverUri;

	protected static ServerLoader apiServer;

	private HttpURLConnection urlConnection;

	private int responseCode;

	private BufferedReader response;

	@BeforeClass
	public static void startServer() {

		try {

			apiServer = new ServerLoader(new String[] { "--port", SERVER_PORT });
			apiServer.launch(false);

			serverUri = new URI(String.format("http://%s:%s", SERVER_HOST, SERVER_PORT));

		} catch (Exception e) {

			Assert.fail("Failed to initialize server.");
		}
	}

	protected void apiGet(String path) {

		try {

			String encoded = URLEncoder.encode(path, "UTF-8");

			URL url = serverUri.resolve("/" + encoded).toURL();
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();

			responseCode = urlConnection.getResponseCode();

			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				response = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			} else {

				response = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
			}

		} catch (MalformedURLException e) {

			Assert.fail("Malformed URL.");
			e.printStackTrace(System.err);
		} catch (IOException e) {

			Assert.fail("IOException.");
			e.printStackTrace(System.err);
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
