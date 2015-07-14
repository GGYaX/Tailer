package me.yanxin.tailserver.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.yanxin.tailserver.TailServerConfiguration;
import me.yanxin.tailserver.TailerHandler;

import org.apache.commons.io.input.Tailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailServiceProcess implements ProcessInterface {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServiceProcess.class);

	private static List<Tailer> TAILERS = new ArrayList<Tailer>();

	private volatile boolean running = false;

	public TailServiceProcess(SocketIOServiceProcess socketIOServiceProcess) {
		// Tailer server
		@SuppressWarnings("rawtypes")
		Iterator it = TailServerConfiguration.FILES_TO_TAIL.entrySet()
				.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> fileToTail = (Map.Entry<String, String>) it
					.next();
			String filename = fileToTail.getKey();
			String filepath = fileToTail.getValue();

			// register tailer
			TAILERS.add(new Tailer(new File(filepath), new TailerHandler(
					filename, socketIOServiceProcess),
					TailServerConfiguration.TAILER_DELAY));
		}
	}

	@Override
	public void terminate() throws Exception {
		for (Tailer tailer : TAILERS) {
			tailer.stop();
		}
		setRunning(false);
		LOGGER.debug("Tail Serivce terminated.");
	}

	@Override
	public void run() {
		for (Tailer tailer : TAILERS) {
			Thread thread = new Thread(tailer);
			thread.setDaemon(true);
			thread.start();
		}
		setRunning(true);
		LOGGER.debug("Tail Service running.");
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
