package org.barcodeapi.server;

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

		Server server = new Server(serverPort);

		HandlerCollection handlers = new HandlerCollection();

		// setup code
		ContextHandler _128Context = new ContextHandler();
		_128Context.setContextPath("/128");
		_128Context.setHandler(new BarcodeServer());
		handlers.addHandler(_128Context);

		// add handlers
		server.setHandler(handlers);

		// start server
		server.start();
		server.join();

	}
}