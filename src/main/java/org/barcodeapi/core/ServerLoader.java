package org.barcodeapi.core;

import org.barcodeapi.server.core.BarcodeServer;
import org.barcodeapi.server.statistics.StatsServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class ServerLoader {

	private int serverPort = 8080;

	private Server server;
	private HandlerCollection handlers;

	/**
	 * Initialize the server loader by parsing the command line arguments supplied
	 * by the user.
	 * 
	 * @param args
	 */
	public ServerLoader(String[] args) {

		parseLaunchArgs(args);
	}

	/**
	 * Main entry point to start the server; jetty will be initialized followed by
	 * each handler. Once initialized the server will be started and ready to server
	 * requests. This is a blocking method that will not return until the server is
	 * shutdown.
	 * 
	 * @throws Exception
	 */
	public void launch() throws Exception {

		initJetty();

		initStatsHandler();

		initApiHandler();

		startServer();
	}

	/**
	 * Loop and parse each provided argument.
	 * 
	 * @param args
	 */
	private void parseLaunchArgs(String[] args) {

		// do nothing if null arguments
		if (args == null) {

			return;
		}

		// loop all arguments
		for (int x = 0; x < args.length; x++) {

			switch (args[x]) {

			case "--port":
				serverPort = Integer.parseInt(args[++x]);
				break;

			default:
				System.err.println("Unknown argument [ " + args[x] + " ]");
				System.exit(1);
				break;
			}
		}
	}

	/**
	 * Initialize the Jetty server.
	 */
	private void initJetty() {

		// initialize handler collection
		handlers = new HandlerCollection();

		// initialize API server
		server = new Server(serverPort);
		server.setHandler(handlers);
	}

	/**
	 * Initialize the statistics end-point.
	 */
	private void initStatsHandler() {

		// setup statistics handler
		ContextHandler statsHandler = new ContextHandler();
		statsHandler.setHandler(new StatsServer());
		statsHandler.setContextPath("/stats");
		handlers.addHandler(statsHandler);
	}

	/**
	 * Initialize the main API handler.
	 */
	private void initApiHandler() {

		// setup API handler
		ContextHandler apiHandler = new ContextHandler();
		apiHandler.setHandler(new BarcodeServer());
		apiHandler.setContextPath("/");
		handlers.addHandler(apiHandler);
	}

	/**
	 * Start the Jetty server.
	 * 
	 * @throws Exception
	 */
	private boolean startServer() {

		try {

			// start server
			server.start();
			server.join();

			return true;
		} catch (Exception e) {

			System.err.println("Failed to start server.");
			e.printStackTrace(System.err);
			return false;
		}
	}
}
