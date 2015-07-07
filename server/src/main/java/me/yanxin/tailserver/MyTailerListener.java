package me.yanxin.tailserver;

import org.apache.commons.io.input.TailerListenerAdapter;

import com.corundumstudio.socketio.SocketIOServer;

public class MyTailerListener extends TailerListenerAdapter {
	private SocketIOServer socketIOServer;
	private String eventName = "message";

	public MyTailerListener(String eventName, SocketIOServer server) {
		super();
		this.eventName = eventName;
		this.socketIOServer = server;
	}

	public SocketIOServer getSocketIOServer() {
		return socketIOServer;
	}

	public void setSocketIOServer(SocketIOServer socketIOServer) {
		this.socketIOServer = socketIOServer;
	}

	@Override
	public void handle(String line) {
		System.out.println("new line:" + line);
		if (socketIOServer != null) {
			socketIOServer.getBroadcastOperations().sendEvent(eventName, line);
		}
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

}
