package org.barcodeapi.core;

import org.barcodeapi.server.api.*;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.tasks.BarcodeCleanupTask;
import org.barcodeapi.server.tasks.LogRotateTask;
import org.barcodeapi.server.tasks.SessionCleanupTask;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

import java.util.concurrent.TimeUnit;

/**
 * This class should handle the processing of the command line arguments passed
 * on startup in addition to the setup of the main Jetty API server and it's
 * associated handlers.
 * 
 * @author Matthew R. Clark, 2020
 *
 */
public class ServerLoader {

	// Initialize server runtime and get ID
	private final String _ID = ServerRuntime.getRuntimeID();

	// The port for the servers to bind to.
	private int serverPort = 8080;

	// Report usage statistics by default
	private boolean telemetry = true;

	// The instance of the running Jetty server and it's handlers.
	private Server server;
	private HandlerCollection handlers;

	/**
	 * Initialize the server loader by processing the command line arguments
	 * supplied by the user.
	 * 
	 * @param args
	 */
	public ServerLoader(String[] args) {

		parseLaunchArgs(args);
	}

	/**
	 * Main entry point to start the server; jetty will be initialized followed by
	 * each handler. Once initialized the server will be started and ready to server
	 * requests.
	 * 
	 * @throws Exception
	 */
	public void launch() throws Exception {

		initApiServer();

		initSystemTasks();

		startServer();
	}

	/**
	 * Loop and parse each provided argument.
	 * 
	 * @param args
	 */
	private void parseLaunchArgs(String[] args) {

		// Do nothing if null arguments
		if (args == null) {

			return;
		}

		// Loop all arguments
		for (int x = 0; x < args.length; x++) {

			switch (args[x]) {

			case "--port":
				serverPort = Integer.parseInt(args[++x]);
				break;

			case "--no-telemetry":
				telemetry = false;
				break;

			default:
				System.err.println("Unknown argument [ " + args[x] + " ]");
				System.exit(1);
				break;
			}
		}
	}

	/**
	 * Initialize the API REST server.
	 */
	private void initApiServer() throws Exception {

		// initialize API server
		Log.out(LOG.SERVER, "Initializing: " + _ID);
		Log.out(LOG.SERVER, "Starting Jetty API Server");
		handlers = new HandlerCollection();
		server = new Server(serverPort);
		server.setHandler(handlers);

		// setup rest handlers
		initHandler("/api", BarcodeAPIHandler.class);
		initHandler("/bulk", BulkHandler.class);
		initHandler("/types", TypesHandler.class);
		initHandler("/session", SessionDetailsHandler.class);

		// setup server handlers
		initHandler("/server/about", AboutHandler.class);

		// Instantiate the static resource handler and add it to the collection
		Log.out(LOG.SERVER, "Initializing static resource handler");
		ContextHandler resourceHandler = new ContextHandler();
		resourceHandler.setHandler(new StaticHandler(server));
		resourceHandler.setContextPath("/");
		handlers.addHandler(resourceHandler);
	}

	/**
	 * Initialize a new handler to be served by Jetty
	 */
	private void initHandler(String path, Class<? extends RestHandler> clazz) throws Exception {

		// Instantiate the handler
		Log.out(LOG.SERVER, "Initializing handler: " + path);
		RestHandler handler = clazz.getConstructor().newInstance();

		// Add it to the handler collection
		ContextHandler statsHandler = new ContextHandler();
		statsHandler.setHandler(handler);
		statsHandler.setContextPath(path);
		handlers.addHandler(statsHandler);
	}

	/**
	 * Initialize the system tasks which run periodically in the background.
	 */
	private void initSystemTasks() {

		// cleanup sessions every 15 minutes
		SessionCleanupTask sessionCleanup = new SessionCleanupTask();
		ServerRuntime.getSystemTimer().schedule(sessionCleanup, 0, //
				TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES));

		// cleanup barcodes every hour
		BarcodeCleanupTask barcodeCleanup = new BarcodeCleanupTask();
		ServerRuntime.getSystemTimer().schedule(barcodeCleanup, 0, //
				TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));

		// rotate logs every 24h
		LogRotateTask logRotate = new LogRotateTask();
		ServerRuntime.getSystemTimer().schedule(logRotate, Log.timeTillRotate(), //
				TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
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
			return true;
		} catch (Exception e) {

			System.err.println("Failed to start server.");
			e.printStackTrace(System.err);
			return false;
		}
	}

	/**
	 * Stop the Jetty server.
	 * 
	 * @throws Exception
	 */
	public boolean stop() {

		try {

			// stop server
			server.stop();
			return true;
		} catch (Exception e) {

			System.err.println("Failed to stop server.");
			e.printStackTrace(System.err);
			return false;
		}
	}
}
