package me.yanxin.tailserver;

import java.util.ArrayList;
import java.util.List;

import me.yanxin.tailserver.process.HttpServerServiceProcess;
import me.yanxin.tailserver.process.ProcessListener;
import me.yanxin.tailserver.process.SocketIOServiceProcess;

import org.apache.commons.io.input.Tailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailServerLauncher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServerLauncher.class);

	public static List<Tailer> tailServices = new ArrayList<Tailer>();

	public static void main(String[] args) throws Exception {
		TailServerConfiguration.load();
		/*
		 * Starting http server [BEGIN]
		 */
		final ProcessListener<HttpServerServiceProcess> httpServerServiceListener;
		HttpServerServiceProcess httpServerServiceProcess = new HttpServerServiceProcess();
		httpServerServiceListener = new ProcessListener<HttpServerServiceProcess>(
				httpServerServiceProcess);
		httpServerServiceListener.initialize();
		/*
		 * Starting http server [END]
		 */

		/*
		 * Starting socket io server[BEGIN]
		 */
		final ProcessListener<SocketIOServiceProcess> socketIOServiceListener;
		SocketIOServiceProcess socketIOServiceProcess = new SocketIOServiceProcess();
		socketIOServiceListener = new ProcessListener<SocketIOServiceProcess>(
				socketIOServiceProcess);
		socketIOServiceListener.initialize();
		/*
		 * Starting socket io server[END]
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			/*
			 * Secure adresse
			 * 
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			public void run() {
				LOGGER.info("Terminating server...");
				httpServerServiceListener.destroy();
				socketIOServiceListener.destroy();
				LOGGER.info("Server is down.");
			}
		});
	}
}
