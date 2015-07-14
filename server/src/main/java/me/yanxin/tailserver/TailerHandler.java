package me.yanxin.tailserver;

import me.yanxin.tailserver.process.SocketIOServiceProcess;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailerHandler extends TailerListenerAdapter {
	private SocketIOServiceProcess socketIOServiceProcess;
	private String eventName;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailerHandler.class);

	public TailerHandler(String eventName, SocketIOServiceProcess server) {
		super();
		this.eventName = eventName;
		this.socketIOServiceProcess = server;
	}

	@Override
	public void handle(String line) {
		LOGGER.debug("new line: " + line);
		if (socketIOServiceProcess != null) {
			socketIOServiceProcess.broadcast(eventName, line);
		}
	}

	@Override
	public void fileNotFound() {
		LOGGER.debug("File not found, file has been rotated?");
	}

	@Override
	public void fileRotated() {
		LOGGER.debug("File has been rotated.");
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

}
