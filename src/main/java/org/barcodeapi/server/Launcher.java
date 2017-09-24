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

		ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
		HandlerCollection handlers = new HandlerCollection();

		// setup code 128 handler
		ContextHandler _128Context = new ContextHandler();
		_128Context.setContextPath("/128");
		_128Context.setHandler(new BarcodeServer());
		_128Context.setErrorHandler(errorHandler);
		handlers.addHandler(_128Context);

		// setup qr handler
		ContextHandler _QRContext = new ContextHandler();
		_QRContext.setContextPath("/qr");
		_QRContext.setHandler(new QRServer());
		_QRContext.setErrorHandler(errorHandler);
		handlers.addHandler(_QRContext);

		// setup matrix handler
		ContextHandler _MatrixHandler = new ContextHandler();
		_MatrixHandler.setContextPath("/matrix");
		_MatrixHandler.setHandler(new MatrixHandler());
		_MatrixHandler.setErrorHandler(errorHandler);
		handlers.addHandler(_MatrixHandler);

		// add handlers to server
		server.setHandler(handlers);

		// start server
		server.start();
		server.join();

	}
}