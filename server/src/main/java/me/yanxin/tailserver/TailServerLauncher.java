package me.yanxin.tailserver;

import java.io.File;

import org.apache.commons.io.input.Tailer;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class TailServerLauncher {

	public static void main(String[] args) throws InterruptedException {
		Configuration config = new Configuration();
		config.setHostname("localhost");
		config.setPort(9092);

		final SocketIOServer server = new SocketIOServer(config);

		File file = new File("/Users/yanxingong/Documents/test.txt");

		MyTailerListener listener = new MyTailerListener("message", server);

		server.start();
		Tailer tailer = new Tailer(file, listener);
		tailer.run();
		//
		Thread.sleep(Integer.MAX_VALUE);

		server.stop();
	}
}
