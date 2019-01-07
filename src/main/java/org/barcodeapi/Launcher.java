package org.barcodeapi;

import org.barcodeapi.core.ServerLoader;

/**
 * This class should handle the master life-cycle of the Barcode API server and
 * sub components thereof.
 * 
 * @author Matthew R. Clark, 2019
 *
 */
public class Launcher {

	// The instance of the running application.
	private static ServerLoader apiServer;

	/**
	 * This method should serve as the main entry point for the application when
	 * being started by any means, it should catch and properly log any fatal
	 * exceptions which might arise at any time in the applications life before it
	 * is properly terminated.
	 * 
	 * @param args
	 *            Any arguments passed to the application from the command line.
	 */
	public static void main(String[] args) {

		// Print startup message
		System.out.println("");
		System.out.println("Starting...");
		System.out.println("");

		try {

			// Instantiate the loader
			apiServer = new ServerLoader(args);

			// Launch the system
			apiServer.launch();

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

	/**
	 * This method may be called by any class, at any time, to request that the
	 * application be terminated with the given exit code.
	 * 
	 * @param exitCode
	 *            The exit code to be returned to the operating system.
	 */
	public static void invokeSystemShutdown(final int exitCode) {

		// Attempt a graceful shutdown the server
		apiServer.stop();

		// Run this in a dedicated thread so the call returns
		new Thread(new Runnable() {

			public void run() {

				// Exit the JVM with a given status code
				System.exit(exitCode);
			}
		}).start();
	}
}