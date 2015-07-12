package me.yanxin.tailserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.Tailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

public class TailServerLauncher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServerLauncher.class);

	public static List<Tailer> tailServices = new ArrayList<Tailer>();

	public static void main(String[] args) throws Exception {

		Configuration socketIOConfig = new Configuration();
		TailServerConfiguration.load();
		socketIOConfig
				.setHostname(TailServerConfiguration.SOCKET_IO_SERVER_HOST);
		socketIOConfig.setPort(TailServerConfiguration.SOCKET_IO_SERVER_PORT);

		final SocketIOServer socketIOServer = new SocketIOServer(socketIOConfig);

		// Tailer server
		@SuppressWarnings("rawtypes")
		Iterator it = TailServerConfiguration.FILES_TO_TAIL.entrySet()
				.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> fileToTail = (Map.Entry<String, String>) it
					.next();
			TailThread tail = new TailThread(fileToTail.getKey(),
					fileToTail.getValue(), socketIOServer);
			Thread tailThread = new Thread(tail);
			tailThread.setDaemon(true);
			tailThread.start();
		}

		/*
		 * Thread to run httpServer
		 */
		final SimpleHttpServerLauncher httpServer = new SimpleHttpServerLauncher(
				TailServerConfiguration.HTTP_SERVER_PORT,
				TailServerConfiguration.HTTP_SERVER_BASE_DIR);
		Thread httpServerThread = new Thread(httpServer);
		httpServerThread.setDaemon(true);
		LOGGER.info("Starting http server on port "
				+ TailServerConfiguration.HTTP_SERVER_PORT);
		httpServerThread.start();

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
				for (Tailer tailer : tailServices) {
					tailer.stop();
				}
				socketIOServer.stop();
				httpServer.stop();
				LOGGER.info("Server is down.");
			}
		});

		socketIOServer.addConnectListener(new ConnectListener() {

			@Override
			public void onConnect(SocketIOClient client) {
				client.sendEvent("init", TailServerConfiguration.FILES_LIST);
			}
		});

		LOGGER.info("Starting socket io server on port "
				+ TailServerConfiguration.SOCKET_IO_SERVER_PORT);
		socketIOServer.start();

		Thread.sleep(Integer.MAX_VALUE);

		socketIOServer.stop();
	}
}
