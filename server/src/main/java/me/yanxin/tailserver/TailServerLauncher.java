package me.yanxin.tailserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.input.Tailer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

public class TailServerLauncher {

	public static Properties properties;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServerLauncher.class);

	public static String SOCKET_IO_SERVER_HOST;
	public static int SOCKET_IO_SERVER_PORT;
	public static String SERVER_MESSAGE_ROOM1;
	public static long TAILER_DELAY;
	public static String TAILER_FILE_NAME;
	public static int HTTP_SERVER_PORT;
	public static String HTTP_SERVER_BASE_DIR;
	public static String FILES_LIST;

	private static JSONObject loadDefaultFile() throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject toReturn = null;
		try {
			Object object = parser.parse(new InputStreamReader(
					TailServerLauncher.class.getClassLoader()
							.getResourceAsStream("files.json")));
			toReturn = (JSONObject) object;
		} catch (IOException e) {
			LOGGER.error("Read file error. See for more details", e);
			throw (e);
		} catch (ParseException e) {
			LOGGER.error("Parse JSON file error. See for more details", e);
			throw (e);
		}
		return toReturn;
	}

	public static void main(String[] args) throws Exception {
		PropertiesLoader.loadFichiersProperties();
		properties = PropertiesLoader.getProperties();

		/*
		 * [BEGIN] Set env varialbes
		 */
		SOCKET_IO_SERVER_HOST = properties.getProperty("default.socketio.host");
		SOCKET_IO_SERVER_PORT = Integer.parseInt(properties
				.getProperty("default.socketio.port"));
		TAILER_DELAY = Long.parseLong(properties
				.getProperty("default.tailer.delay"));
		TAILER_FILE_NAME = properties.getProperty("default.tailer.fileToRead");
		SERVER_MESSAGE_ROOM1 = properties.getProperty("default.socketio.room1");
		HTTP_SERVER_PORT = Integer.parseInt(properties
				.getProperty("default.httpserver.port"));
		HTTP_SERVER_BASE_DIR = properties
				.getProperty("default.httpserver.basedir");
		FILES_LIST = loadDefaultFile().toJSONString();
		/*
		 * [END] Set env varialbes
		 */

		Configuration socketIOConfig = new Configuration();
		socketIOConfig.setHostname(SOCKET_IO_SERVER_HOST);
		socketIOConfig.setPort(SOCKET_IO_SERVER_PORT);

		final SocketIOServer socketIOServer = new SocketIOServer(socketIOConfig);
		long delay = 500;

		File file = new File(TAILER_FILE_NAME);
		MyTailerListener listener = new MyTailerListener(SERVER_MESSAGE_ROOM1,
				socketIOServer);
		final Tailer tailer = new Tailer(file, listener, delay);

		// Thread to run httpServer
		final SimpleHttpServerLauncher httpServer = new SimpleHttpServerLauncher(
				HTTP_SERVER_PORT, HTTP_SERVER_BASE_DIR);
		Thread httpServerThread = new Thread(httpServer);
		httpServerThread.setDaemon(true);
		LOGGER.info("Starting http server on port " + HTTP_SERVER_PORT);
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
				LOGGER.info("Terminating tailer amd socket.io server");
				tailer.stop();
				socketIOServer.stop();
				httpServer.stop();
			}
		});

		socketIOServer.addConnectListener(new ConnectListener() {

			@Override
			public void onConnect(SocketIOClient client) {
				client.sendEvent("init", FILES_LIST);
			}
		});

		LOGGER.info("Starting socket io server on port "
				+ SOCKET_IO_SERVER_PORT);
		socketIOServer.start();

		tailer.run();

		Thread.sleep(Integer.MAX_VALUE);

		socketIOServer.stop();
		tailer.stop();
	}
}
