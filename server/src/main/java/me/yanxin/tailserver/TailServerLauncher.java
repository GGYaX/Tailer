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
		long delay = 500;

		File file = new File("/Users/yanxingong/Documents/test.txt");
		MyTailerListener listener = new MyTailerListener("message", server);
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
