package org.barcodeapi.server;

import org.eclipse.jetty.server.Server;

public class Launcher {

	public static void main(String[] args) throws Exception {

		Server server = new Server(8080);
		server.setHandler(new BarcodeServer());

		server.start();
		server.join();
	}
}