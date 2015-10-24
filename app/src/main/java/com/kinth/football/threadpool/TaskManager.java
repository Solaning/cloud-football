package com.kinth.football.threadpool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理类
 * 其中downloadTasks表示的是线程队列，taskIdSet是任务队列，作用就是用来管理线程队列，此程序用的是去重操作
 * 不会再次下载
 * 
 * @author Sola
 * 
 */
public class TaskManager {
	// 任务不能重复
	private Set<String> taskIdSet;
	private static Object object = new Object();

	private static TaskManager downloadTaskMananger;
	
	// 创建一个可重用固定线程数的线程池
	private ExecutorService pool;
	// 线程池大小
	private final int POOL_SIZE = 5;

	private TaskManager() {
		taskIdSet = new HashSet<String>();
		if(pool == null)
			pool = Executors.newFixedThreadPool(POOL_SIZE);
	}

	public static synchronized TaskManager getInstance() {
		if (downloadTaskMananger == null) {
			downloadTaskMananger = new TaskManager();
		}
		return downloadTaskMananger;
	}

	public void addDownloadTask(ParentTask downloadTask) {
		synchronized (object) {
			if (!isTaskRepeat(downloadTask.getTaskId())) {
				// 增加任务
				pool.execute(downloadTask);
			}
		}
	}

	public boolean isTaskRepeat(String taskId) {
		if (taskIdSet.contains(taskId)) {
			return true;
		} else {
			taskIdSet.add(taskId);
			return false;
		}
	}
	
	public void setStop() {
		pool.shutdown();
	}
}
