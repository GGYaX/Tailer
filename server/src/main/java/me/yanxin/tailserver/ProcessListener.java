package me.yanxin.tailserver;

import me.yanxin.tailserver.process.ProcessInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessListener<T extends ProcessInterface> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProcessListener.class);

	private Thread thread;
	private T runnable;

	public ProcessListener(T runnable) {
		this.runnable = runnable;
	}

	public void initialize() {
		thread = new Thread(runnable);
		LOGGER.debug("Starting thread: " + thread);
		thread.start();
		LOGGER.debug("Background process successfully started.");
	}

	public void destroy() {
		LOGGER.debug("Stopping thread: " + thread);
		if (thread != null) {
			try {
				runnable.terminate();
				thread.join();
				LOGGER.debug("Thread successfully stopped.");
			} catch (Exception e) {
				LOGGER.error(
						"Stopping/Destroying thread exception. See for more details.",
						e);
			}
		}
	}
}
