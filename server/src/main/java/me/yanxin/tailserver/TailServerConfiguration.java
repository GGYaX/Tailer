package me.yanxin.tailserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailServerConfiguration {
	private static Properties properties;
	public static String SOCKET_IO_SERVER_HOST;
	public static int SOCKET_IO_SERVER_PORT;
	// TODO to delete
	public static String SERVER_MESSAGE_ROOM1;
	public static long TAILER_DELAY;
	public static int HTTP_SERVER_PORT;
	public static String HTTP_SERVER_BASE_DIR;
	public static String FILES_LIST;
	public static JSONObject FILES_TO_TAIL;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServerConfiguration.class);

	private static JSONObject loadDefaultFile() throws RuntimeException {
		JSONParser parser = new JSONParser();
		JSONObject toReturn = null;
		try {
			Object object = parser.parse(new InputStreamReader(
					TailServerLauncher.class.getClassLoader()
							.getResourceAsStream("files.json")));
			toReturn = (JSONObject) object;
		} catch (IOException e) {
			LOGGER.error("Read file error. See for more details", e);
		} catch (ParseException e) {
			LOGGER.error("Parse JSON file error. See for more details", e);
		}
		return toReturn;
	}

	public static void load() {
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
		SERVER_MESSAGE_ROOM1 = properties.getProperty("default.socketio.room1");
		HTTP_SERVER_PORT = Integer.parseInt(properties
				.getProperty("default.httpserver.port"));
		HTTP_SERVER_BASE_DIR = properties
				.getProperty("default.httpserver.basedir");
		FILES_TO_TAIL = loadDefaultFile();
		FILES_LIST = FILES_TO_TAIL.toJSONString();
		/*
		 * [END] Set env varialbes
		 */
	}

}
