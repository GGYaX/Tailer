package me.yanxin.tailserver.process;

public interface ProcessInterface extends Runnable {
	public abstract void terminate() throws Exception;

	public abstract boolean isRunning();
}
