package org.barcodeapi.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.barcodeapi.server.api.BarcodeAPIHandler;
import org.barcodeapi.server.api.BarcodeTypeHandler;
import org.barcodeapi.server.api.ServerInfoHandler;
import org.barcodeapi.server.api.ServerStatsHandler;
import org.barcodeapi.server.api.StaticHandler;
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
import com.mclarkdev.tools.libloggelf.lib.LibLogGELFLogWriter;

/**
 * ServerLauncher.java
 * 
 * This class should handle the processing of the command line arguments passed
 * on startup in addition to the setup of the main Jetty API server and it's
 * associated handlers.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2026, Community Edition)
 */
public class ServerLauncher {

	static {
		LibLog._logF("Runtime ID: %s", ServerRuntime.getRuntimeID());
		LibLog.cfg().registerLogger(LibLogGELFLogWriter.class);
	}

	// The Jetty server and it's handlers
	private Server server;
	private HashMap<String, RestHandler> handlers = new HashMap<>();
	private ArrayList<BackgroundTask> tasks = new ArrayList<>();

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
		LibLog.cfg().loadStrings(ServerLauncher.class.getResourceAsStream(//
				String.format("/strings/codes.%s.properties", lang)));

		// Log config distribution being used
		LibLog._clogF("I0000", Config.dist());
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
	protected void initApiServer() throws Exception {
		CodeGenerators.getInstance();

		// Setup rest handlers
		registerEndpoint("/api", BarcodeAPIHandler.class);
		registerEndpoint("/type", BarcodeTypeHandler.class);

		// Server Stats
		registerEndpoint("/server/info", ServerInfoHandler.class);
		registerEndpoint("/server/stats", ServerStatsHandler.class);
	}

	/**
	 * Initialize a new handler to be served by Jetty
	 */
	protected void registerEndpoint(String path, //
			Class<? extends RestHandler> clazz) throws Exception {

		// Instantiate the handler
		LibLog._clogF("I0021", path);
		handlers.put(path, //
				clazz.getConstructor().newInstance());
	}

	/**
	 * Initialize the system tasks which run periodically in the background.
	 */
	private void initSystemTasks() {

		JSONArray taskList = Config.get().getJSONArray("tasks");

		// Loop each of the registered tasks
		for (int x = 0; x < taskList.length(); x++) {
			JSONObject taskDef = taskList.getJSONObject(x);

			try {

				// Get task details
				String taskName = taskDef.getString("name");
				String taskImpl = taskDef.getString("impl");
				String taskRoot = ((taskImpl.charAt(0) == '.') ? BackgroundTask.TASKROOT : "");

				String taskClass = (taskRoot + taskImpl);
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
	}

	/**
	 * Start the Jetty server.
	 * 
	 * @throws Exception
	 */
	private void startServer() throws Exception {

		try {

			HandlerCollection collection = new HandlerCollection();

			// Loop all registered handlers
			for (Map.Entry<String, RestHandler> entry : handlers.entrySet()) {

				// Add it to the handler collection
				ContextHandler contextHandler = new ContextHandler();
				contextHandler.setHandler(entry.getValue());
				contextHandler.setContextPath(entry.getKey());
				collection.addHandler(contextHandler);
			}

			// Initialize API server
			LibLog._clog("I0011");
			server = new Server();
			server.setHandler(collection);

			// Instantiate the static resource handler and add it to the collection
			LibLog._clog("I0012");
			ContextHandler resourceHandler = new ContextHandler();
			resourceHandler.setHandler(new StaticHandler(server));
			resourceHandler.setContextPath("/");
			collection.addHandler(resourceHandler);

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

			// Start server
			server.start();
		} catch (Exception e) {

			// Log the startup error
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

		// Skip if not exists
		if (server == null) {
			return false;
		}

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
