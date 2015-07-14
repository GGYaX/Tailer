package me.yanxin.tailserver.process;

import java.util.UUID;

import me.yanxin.tailserver.TailServerConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

public class SocketIOServiceProcess implements ProcessInterface {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SocketIOServiceProcess.class);

	private volatile boolean running = false;

	private SocketIOServer socketIOServer;

	private static final String INIT_EVENT_NAME = "init";

	public SocketIOServiceProcess() {
		Configuration socketIOConfig = new Configuration();
		socketIOConfig
				.setHostname(TailServerConfiguration.SOCKET_IO_SERVER_HOST);
		socketIOConfig.setPort(TailServerConfiguration.SOCKET_IO_SERVER_PORT);
		socketIOServer = new SocketIOServer(socketIOConfig);
	}

	@Override
	public void terminate() throws Exception {
		socketIOServer.stop();
		setRunning(false);
		LOGGER.debug("Socket IO Server Service terminated.");
	}

	@Override
	public void run() {

		socketIOServer.addConnectListener(new ConnectListener() {
			@Override
			public void onConnect(SocketIOClient client) {
				client.sendEvent(INIT_EVENT_NAME,
						TailServerConfiguration.FILES_LIST);
			}
		});

		socketIOServer.start();
		setRunning(true);
		LOGGER.debug("Socket IO Server Service running.");
	}

	public void broadcast(String eventName, Object data) {
		if (isRunning()) {
			LOGGER.debug("Broadcasting to room: " + eventName);
			socketIOServer.getBroadcastOperations().sendEvent(eventName, data);
		} else {
			LOGGER.debug("Socket io not running.");
		}
	}

	public void sendMessageToSomeOne(String eventName, UUID to, Object message) {
		if (isRunning()) {
			SocketIOClient client = socketIOServer.getClient(to);
			if (client != null) {
				client.sendEvent(eventName, message);
			} else {
				LOGGER.error("Client {0} does not exists, send message fault.",
						to);
			}
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
