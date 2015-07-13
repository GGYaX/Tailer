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

	private volatile boolean running = true;

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
		LOGGER.debug("Socket IO Server terminated.");
		setRunning(false);
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
	}

	public void broadcast(String eventName, Object data) {
		if (isRunning()) {
			socketIOServer.getBroadcastOperations().sendEvent(eventName, data);
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

	public boolean isRunning() {
		if (!running) {
			LOGGER.debug("Socket io not running.");
		}
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
