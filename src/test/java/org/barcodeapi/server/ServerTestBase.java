package org.barcodeapi.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.barcodeapi.core.ServerLoader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public abstract class ServerTestBase {

	protected static URI serverUri;

	protected static ServerLoader apiServer;

	@BeforeClass
	public static void startServer() {

		try {

			apiServer = new ServerLoader(null);
			apiServer.launch(false);

			serverUri = new URI("http://127.0.0.1:8080/");

		} catch (Exception e) {

			Assert.fail("Failed to initialize server.");
		}
	}

	protected HttpURLConnection apiGet(String path) {

		try {

			URL url = serverUri.resolve(path).toURL();
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.connect();

			return http;

		} catch (MalformedURLException e) {

			Assert.fail("Malformed URL.");
			e.printStackTrace(System.err);
			return null;
		} catch (IOException e) {

			Assert.fail("IOException.");
			e.printStackTrace(System.err);
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
