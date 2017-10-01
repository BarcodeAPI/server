package org.barcodeapi;

import org.barcodeapi.core.ServerLoader;

public class Launcher {

	private static ServerLoader loader;

	public static void main(String[] args) throws Exception {

		// Print startup message
		System.out.println("");
		System.out.println("Starting...");
		System.out.println("");

		try {

			// Instantiate the loader
			loader = new ServerLoader(args);

			// Launch the system
			loader.launch(true);

		} catch (Exception e) {

			// Log launch exception and quit
			e.printStackTrace(System.err);
			Launcher.invokeSystemShutdown(1);

		} catch (NoClassDefFoundError e) {

			// Log error and quit on launch dependency failure
			System.out.println("Missing required libraries. Exiting.");
			System.err.println("Missing required libraries. Exiting.");
			e.printStackTrace(System.err);
			Launcher.invokeSystemShutdown(1);
		}
	}

	public static void invokeSystemShutdown(final int statusCode) {

		// Run this in a dedicated thread so the call returns
		new Thread(new Runnable() {

			public void run() {

				// Exit the JVM with a given status code
				System.exit(statusCode);
			}
		}).start();
	}
}