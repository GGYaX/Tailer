package me.yanxin.tailserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpServerLauncher implements Runnable {

	private Server httpServer;

	private String baseDir;

	private int port;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleHttpServerLauncher.class);

	public SimpleHttpServerLauncher(int port, String baseDir) {
		this.baseDir = baseDir;
		this.port = port;
		httpServer = new Server(port);
	}

	@Override
	public void run() {

		/*
		 * File server configuration [BEGIN]
		 */

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });

		resource_handler.setResourceBase(baseDir);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler,
				new DefaultHandler() });
		httpServer.setHandler(handlers);

		/*
		 * File server configuration [END]
		 */
		try {
			httpServer.start();
		} catch (Exception e) {
			LOGGER.error("Starting http server on port" + this.port
					+ "failed. Please see details.", e);
			e.printStackTrace();
			httpServer.destroy();
		}
		try {
			httpServer.join();
		} catch (InterruptedException e) {
			LOGGER.error(
					"Http server has a problem during excution. Please see details.",
					e);
			httpServer.destroy();
		}
	}

	public void stop() {
		try {
			httpServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpServer.destroy();
		}
	}

}
