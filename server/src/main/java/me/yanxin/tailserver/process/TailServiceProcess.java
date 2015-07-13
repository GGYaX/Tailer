package me.yanxin.tailserver.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailServiceProcess implements ProcessInterface {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TailServiceProcess.class);

	private volatile boolean running = true;

	@Override
	public void terminate() throws Exception {
		running = false;
	}

	@Override
	public void run() {
		while (running) {
			try {
				LOGGER.debug("yes");
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				LOGGER.error("Thread interrupted. See for more details :", e);
				running = false;
			}
		}

	}

}
