package org.barcodeapi.server;

import org.barcodeapi.server.cache.ImageCache;
import org.barcodeapi.server.core.BarcodeServer;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.statistics.StatsServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class Launcher {

	public static void main(String[] args) throws Exception {

		int serverPort = 8080;
		try {

			serverPort = Integer.parseInt(args[0]);
		} catch (IndexOutOfBoundsException e) {

		} catch (NumberFormatException e) {

			System.err.println("Invalid port argument.");
			System.exit(1);
		}

		System.out.println("Binding to port [ " + serverPort + " ]");
		Server server = new Server(serverPort);

		// create code caches
		ImageCache.getInstance().createCache(CodeType.Code39);
		ImageCache.getInstance().createCache(CodeType.Code128);
		ImageCache.getInstance().createCache(CodeType.QRCode);
		ImageCache.getInstance().createCache(CodeType.DataMatrix);

		HandlerCollection handlers = new HandlerCollection();

		// setup stats handler
		ContextHandler statsHandler = new ContextHandler();
		statsHandler.setHandler(new StatsServer());
		statsHandler.setContextPath("/stats");
		handlers.addHandler(statsHandler);

		// setup default handler
		ContextHandler apiHandler = new ContextHandler();
		apiHandler.setHandler(new BarcodeServer());
		apiHandler.setContextPath("/");
		handlers.addHandler(apiHandler);

		// add handlers to server
		server.setHandler(handlers);

		// start server
		server.start();
		server.join();
	}
}