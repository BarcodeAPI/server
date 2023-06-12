package org.barcodeapi.core;

import java.util.concurrent.TimeUnit;

import org.barcodeapi.server.admin.CacheDumpHandler;
import org.barcodeapi.server.admin.CacheFlushHandler;
import org.barcodeapi.server.admin.ConfigReloadHandler;
import org.barcodeapi.server.admin.SessionFlushHandler;
import org.barcodeapi.server.admin.SessionListHandler;
import org.barcodeapi.server.api.AboutHandler;
import org.barcodeapi.server.api.BarcodeAPIHandler;
import org.barcodeapi.server.api.BulkHandler;
import org.barcodeapi.server.api.SessionDetailsHandler;
import org.barcodeapi.server.api.StaticHandler;
import org.barcodeapi.server.api.StatsHandler;
import org.barcodeapi.server.api.TypesHandler;
import org.barcodeapi.server.core.Log;
import org.barcodeapi.server.core.Log.LOG;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.tasks.BarcodeCleanupTask;
import org.barcodeapi.server.tasks.SessionCleanupTask;
import org.barcodeapi.server.tasks.StatsDumpTask;
import org.barcodeapi.server.tasks.WatchdogTask;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

import com.mclarkdev.tools.libargs.LibArgs;

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

	// The Jetty server and it's handlers
	private Server server;
	private HandlerCollection handlers;

	private LibArgs libArgs;

	/**
	 * Initialize the server loader by processing the command line arguments
	 * supplied by the user.
	 * 
	 * @param args
	 */
	public ServerLoader(String[] args) {
		this.libArgs = new LibArgs(args);
//		LibArgs.instance().parse(args);
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
	 * Initialize the API REST server.
	 */
	private void initApiServer() throws Exception {

		// initialize API server
		Log.out(LOG.SERVER, "Initializing: " + _ID);
		Log.out(LOG.SERVER, "Starting Jetty API Server");
		handlers = new HandlerCollection();
		server = new Server(libArgs.getInteger("port", 8080));
		server.setHandler(handlers);

		// setup rest handlers
		initHandler("/api", BarcodeAPIHandler.class);
		initHandler("/bulk", BulkHandler.class);
		initHandler("/types", TypesHandler.class);
		initHandler("/session", SessionDetailsHandler.class);

		// setup server handlers
		initHandler("/server/about", AboutHandler.class);
		initHandler("/server/stats", StatsHandler.class);

		// setup admin handlers
		initHandler("/admin/cache/dump", CacheDumpHandler.class);
		initHandler("/admin/cache/flush", CacheFlushHandler.class);

		initHandler("/admin/session/list", SessionListHandler.class);
		initHandler("/admin/session/flush", SessionFlushHandler.class);

		initHandler("/admin/server/reload", ConfigReloadHandler.class);

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
		// run watch-dog every 1 minute
		WatchdogTask watchdogTask = new WatchdogTask();
		ServerRuntime.getSystemTimer().schedule(watchdogTask, 0, //
				TimeUnit.MILLISECONDS.convert(15, TimeUnit.SECONDS));

		// print stats to log every 5 minutes
		StatsDumpTask statsTask = new StatsDumpTask(//
				libArgs.getBoolean("no-telemetry"));
		ServerRuntime.getSystemTimer().schedule(statsTask, 0, //
				TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));

		// cleanup sessions every 15 minutes
		SessionCleanupTask sessionCleanup = new SessionCleanupTask();
		ServerRuntime.getSystemTimer().schedule(sessionCleanup, 0, //
				TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES));

		// cleanup barcodes every hour
		BarcodeCleanupTask barcodeCleanup = new BarcodeCleanupTask();
		ServerRuntime.getSystemTimer().schedule(barcodeCleanup, 0, //
				TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));
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
