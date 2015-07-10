package me.yanxin.tailserver;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.input.Tailer;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class TailServerLauncher {

	public static Properties properties;

	public static String SERVER_HOST;
	public static int SERVER_PORT;
	public static String SERVER_MESSAGE_ROOM1;
	public static long TAILER_DELAY;
	public static String TAILER_FILE_NAME;

	public static void main(String[] args) throws InterruptedException {
		PropertiesLoader.loadFichiersProperties();
		properties = PropertiesLoader.getProperties();

		/*
		 * [BEGIN] Set env varialbes
		 */
		SERVER_HOST = properties.getProperty("default.socketio.host");
		SERVER_PORT = Integer.parseInt(properties
				.getProperty("default.socketio.port"));
		TAILER_DELAY = Long.parseLong(properties
				.getProperty("default.tailer.delay"));
		TAILER_FILE_NAME = properties.getProperty("default.tailer.fileToRead");
		SERVER_MESSAGE_ROOM1 = properties.getProperty("default.socketio.room1");
		/*
		 * [END] Set env varialbes
		 */

		Configuration config = new Configuration();
		config.setHostname(SERVER_HOST);
		config.setPort(SERVER_PORT);

		final SocketIOServer server = new SocketIOServer(config);
		long delay = 500;

		File file = new File(TAILER_FILE_NAME);
		MyTailerListener listener = new MyTailerListener(SERVER_MESSAGE_ROOM1,
				server);
		final Tailer tailer = new Tailer(file, listener, delay);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			/*
			 * Secure adresse
			 * 
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			public void run() {
				System.out.println("Terminating tailer amd socket.io server");
				tailer.stop();
				server.stop();
			}
		});

		server.start();

		tailer.run();

		Thread.sleep(Integer.MAX_VALUE);

		server.stop();
	}
}
