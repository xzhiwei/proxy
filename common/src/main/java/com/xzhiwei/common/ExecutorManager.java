package com.xzhiwei.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ExecutorManager {

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactoryBuilder().setNameFormat("request-%d").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static ThreadPoolExecutor handler = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactoryBuilder().setNameFormat("handler-%d").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

}
