package me.yanxin.tailserver;

import java.util.ArrayList;
import java.util.List;

import me.yanxin.tailserver.process.HttpServerServiceProcess;
import me.yanxin.tailserver.process.ProcessListener;
import me.yanxin.tailserver.process.SocketIOServiceProcess;
import me.yanxin.tailserver.process.TailServiceProcess;

import org.apache.commons.io.input.Tailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailServerLauncher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServerLauncher.class);

	public static List<TailServiceProcess> listTailServiceProcesses = new ArrayList<TailServiceProcess>();

	public static List<Tailer> tailServices = new ArrayList<Tailer>();

	public static void main(String[] args) throws Exception {
		TailServerConfiguration.load();
		/*
		 * Starting http server service [BEGIN]
		 */
		final ProcessListener<HttpServerServiceProcess> httpServerServiceListener;
		HttpServerServiceProcess httpServerServiceProcess = new HttpServerServiceProcess();
		httpServerServiceListener = new ProcessListener<HttpServerServiceProcess>(
				httpServerServiceProcess);
		httpServerServiceListener.initialize();
		/*
		 * Starting http server service [END]
		 */

		/*
		 * Starting socket io server service[BEGIN]
		 */
		final ProcessListener<SocketIOServiceProcess> socketIOServiceListener;
		SocketIOServiceProcess socketIOServiceProcess = new SocketIOServiceProcess();
		socketIOServiceListener = new ProcessListener<SocketIOServiceProcess>(
				socketIOServiceProcess);
		socketIOServiceListener.initialize();
		/*
		 * Starting socket io server service[END]
		 */
		/*
		 * Starting Tail Service[BEGIN]
		 */
		final ProcessListener<TailServiceProcess> tailServiceListener;
		TailServiceProcess tailServiceProcess = new TailServiceProcess(
				socketIOServiceProcess);
		tailServiceListener = new ProcessListener<TailServiceProcess>(
				tailServiceProcess);
		tailServiceListener.initialize();
		/*
		 * Starting Tail Service[END]
		 */

		LOGGER.info("Server is fully running on port "
				+ TailServerConfiguration.HTTP_SERVER_PORT
				+ "see in http://localhost:"
				+ TailServerConfiguration.HTTP_SERVER_PORT + "/client");

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
				tailServiceListener.destroy();
				httpServerServiceListener.destroy();
				socketIOServiceListener.destroy();
				LOGGER.info("Server is down.");
			}
		});
	}
}
