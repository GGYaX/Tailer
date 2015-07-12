package me.yanxin.tailserver;

import java.io.File;

import org.apache.commons.io.input.Tailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOServer;

public class TailThread implements Runnable {
	private SocketIOServer socketIOServer;
	private String filepath;
	private String eventName;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailThread.class);

	public TailThread(String filename, String filepath,
			SocketIOServer socketIOServer) {
		this.socketIOServer = socketIOServer;
		this.filepath = filepath;
		this.eventName = filename;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		MyTailerListener listener = new MyTailerListener(eventName,
				socketIOServer);
		File file = new File(filepath);

		if (file.isDirectory()) {
			LOGGER.error(filepath + "is a directory but not a readable file.");
		} else {
			if (!file.exists()) {
				LOGGER.error(filepath + "does not exit.");
			} else {
				final Tailer tailer = new Tailer(file, listener,
						TailServerConfiguration.TAILER_DELAY);
				// register tailer into launcher
				TailServerLauncher.tailServices.add(tailer);
				tailer.run();
			}
		}

	}
}
