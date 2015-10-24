package com.kinth.football.threadpool;

/**
 * 任务的父类
 * 
 * @author Sola
 *
 */
public abstract class ParentTask implements Runnable {
	public String key;

	public ParentTask(String key) {
		this.key = key;
	}

	public String getTaskId() {
		return key;
	}
}
