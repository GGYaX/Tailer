package me.yanxin.tailserver.process;

import me.yanxin.tailserver.TailServerConfiguration;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerServiceProcess implements ProcessInterface {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpServerServiceProcess.class);

	private volatile boolean running = true;

	private Server httpServer;

	private String baseDir;

	private int port;

	public HttpServerServiceProcess() {
		this.baseDir = TailServerConfiguration.HTTP_SERVER_BASE_DIR;
		this.port = TailServerConfiguration.HTTP_SERVER_PORT;
		httpServer = new Server(port);
	}

	@Override
	public void terminate() throws Exception {
		if (httpServer != null && httpServer.isStarted()) {
			httpServer.stop();
			httpServer.destroy();
		}
		LOGGER.debug("HttpServer terminated");
		setRunning(false);
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
			LOGGER.debug("HttpServer running...");
		} catch (InterruptedException e) {
			LOGGER.error("Thread interrupted. See for more details :", e);
			setRunning(false);
		} catch (Exception e) {
			LOGGER.error("Starting http server on port" + this.port
					+ "failed. Please see details.", e);
			setRunning(false);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
