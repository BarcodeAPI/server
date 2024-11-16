package org.barcodeapi.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Locale;

import org.barcodeapi.server.admin.CacheDumpHandler;
import org.barcodeapi.server.admin.CacheFlushHandler;
import org.barcodeapi.server.admin.LimiterFlushHandler;
import org.barcodeapi.server.admin.LimiterListHandler;
import org.barcodeapi.server.admin.ServerReloadHandler;
import org.barcodeapi.server.admin.ServerStatsHandler;
import org.barcodeapi.server.admin.SessionFlushHandler;
import org.barcodeapi.server.admin.SessionListHandler;
import org.barcodeapi.server.admin.ShareListHandler;
import org.barcodeapi.server.api.BarcodeAPIHandler;
import org.barcodeapi.server.api.BulkHandler;
import org.barcodeapi.server.api.DecodeHandler;
import org.barcodeapi.server.api.InfoHandler;
import org.barcodeapi.server.api.SessionHandler;
import org.barcodeapi.server.api.ShareHandler;
import org.barcodeapi.server.api.StaticHandler;
import org.barcodeapi.server.api.TypeHandler;
import org.barcodeapi.server.api.TypesHandler;
import org.barcodeapi.server.cache.ObjectCache;
import org.barcodeapi.server.core.BackgroundTask;
import org.barcodeapi.server.core.CodeGenerators;
import org.barcodeapi.server.core.RestHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mclarkdev.tools.libargs.LibArgs;
import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libloggelf.LibLogGELF;

/**
 * ServerLauncher.java
 * 
 * This class should handle the processing of the command line arguments passed
 * on startup in addition to the setup of the main Jetty API server and it's
 * associated handlers.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class ServerLauncher {

	static {
		LibLog._logF("Runtime ID: %s", ServerRuntime.getRuntimeID());
		LibLog._logF("Network Logging: %s", LibLogGELF.enabled());
	}

	// The Jetty server and it's handlers
	private Server server;
	private HandlerCollection handlers;
	private ArrayList<BackgroundTask> tasks;

	/**
	 * Initialize the server loader by processing the command line arguments
	 * supplied by the user.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public ServerLauncher(String[] args) throws Exception {

		// Parse command line arguments
		LibArgs.instance().parse(args);

		// Get system language
		String lang = LibArgs.instance().getString(//
				"language", Locale.getDefault().toString());

		// Load localized message codes
		LibLog._logF("Loading Language Pack: %s", lang);
		LibLog.loadStrings(ServerLauncher.class.getResourceAsStream(//
				String.format("/strings/codes.%s.properties", lang)));
	}

	/**
	 * Main entry point to start the server; Jetty will be initialized followed by
	 * each handler. Once initialized the server will be started and ready to server
	 * requests.
	 * 
	 * @throws Exception
	 */
	public void launch() throws Exception {

		// Initialize API server
		LibLog._clog("I0001");
		initApiServer();

		// Start system tasks
		LibLog._clog("I0002");
		initSystemTasks();

		// Start the server
		LibLog._clog("I0003");
		startServer();
	}

	/**
	 * Initialize the API REST server.
	 */
	private void initApiServer() throws Exception {
		CodeGenerators.getInstance();

		// Initialize API server
		LibLog._clog("I0011");
		server = new Server();
		handlers = new HandlerCollection();
		server.setHandler(handlers);

		// Set max request size
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setRequestHeaderSize(16 * 1024);
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", -1);

		// Bind server port
		int portAPI = LibArgs.instance().getInteger("port", 8080);
		ServerConnector serverConnector = new ServerConnector(//
				server, new HttpConnectionFactory(httpConfig));
		serverConnector.setPort(portAPI);
		server.setConnectors(new Connector[] { serverConnector });

		// Setup rest handlers
		initHandler("/api", BarcodeAPIHandler.class);
		initHandler("/bulk", BulkHandler.class);
		initHandler("/decode", DecodeHandler.class);
		initHandler("/type", TypeHandler.class);
		initHandler("/types", TypesHandler.class);
		initHandler("/share", ShareHandler.class);
		initHandler("/session", SessionHandler.class);
		initHandler("/info", InfoHandler.class);

		// Setup admin handlers
		initHandler("/admin/share/list", ShareListHandler.class);
		initHandler("/admin/cache/dump", CacheDumpHandler.class);
		initHandler("/admin/cache/flush", CacheFlushHandler.class);
		initHandler("/admin/limiter/list", LimiterListHandler.class);
		initHandler("/admin/limiter/flush", LimiterFlushHandler.class);
		initHandler("/admin/session/list", SessionListHandler.class);
		initHandler("/admin/session/flush", SessionFlushHandler.class);
		initHandler("/admin/server/reload", ServerReloadHandler.class);

		// Server Stats
		initHandler("/server/stats", ServerStatsHandler.class);

		// Instantiate the static resource handler and add it to the collection
		LibLog._clog("I0012");
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
		LibLog._clogF("I0021", path);
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

		final String TASK_ROOT = "org.barcodeapi.server.tasks";
		JSONArray taskList = AppConfig.get().getJSONArray("tasks");

		tasks = new ArrayList<>();
		for (int x = 0; x < taskList.length(); x++) {
			JSONObject taskDef = taskList.getJSONObject(x);

			try {

				// Get task details
				String taskName = taskDef.getString("name");
				String taskClass = (TASK_ROOT + taskDef.getString("impl"));
				long taskTime = taskDef.getInt("interval");

				// Get task constructor
				@SuppressWarnings("unchecked")
				Constructor<? extends BackgroundTask> constructor = //
						((Class<? extends BackgroundTask>) Class.forName(taskClass)).getDeclaredConstructor();

				// Create and schedule the task
				LibLog._clogF("I0036", taskName);
				BackgroundTask task = constructor.newInstance();
				ServerRuntime.getSystemTimer().schedule(task, 500, (taskTime * 1000));
				tasks.add(task);
			} catch (Exception | Error e) {

				LibLog._clog("E0039", e);
			}
		}

		// Register shutdown hook
		LibLog._clog("I0031");
		Runtime.getRuntime().addShutdownHook(shutdownRunner);
	}

	/**
	 * Hook called as the JVM is shutting down.
	 */
	private Thread shutdownRunner = new Thread() {

		public void run() {

			// Run each task
			LibLog._log("Running shutdown tasks.");

			// Loop all caches
			String[] caches = ObjectCache.getCacheNames();
			for (int x = 0; x < caches.length; x++) {
				try {

					// Lookup the cache object
					ObjectCache cache = ObjectCache.getCache(caches[x]);

					// Save a snapshot and log it
					int saved = cache.saveSnapshot();
					LibLog._clogF("I2602", cache.getName(), saved);
				} catch (IOException e) {

					// Log snapshot failure
					LibLog._clog("E2602", e);
				}
			}
		}
	};

	/**
	 * Start the Jetty server.
	 * 
	 * @throws Exception
	 */
	private void startServer() throws Exception {

		try {

			// Start server
			server.start();
		} catch (Exception e) {

			LibLog._clog("E0008", e);
			throw e;
		}
	}

	/**
	 * Stop the Jetty server.
	 * 
	 * @throws Exception
	 */
	public boolean stop() {

		try {

			// Stop server
			server.stop();
			return true;
		} catch (Exception e) {

			LibLog._clog("E0009", e);
			return false;
		}
	}
}
