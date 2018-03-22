package com.imooc.crawler.factory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadFactory {

	public static ThreadPoolExecutor getThreadPool() {
		return new ThreadPoolExecutor(5, 10, 500, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5));
	}
}
