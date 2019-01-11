package com.xzhiwei.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ExecutorManager {

    public static ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactoryBuilder().setNameFormat("request-%d").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static ExecutorService handler = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactoryBuilder().setNameFormat("handler-%d").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

}
