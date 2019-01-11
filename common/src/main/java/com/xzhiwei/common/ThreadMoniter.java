package com.xzhiwei.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadMoniter {

    private static final Logger logger = LoggerFactory.getLogger(ThreadMoniter.class);

    public static void moniter(){
        new Thread(()->{
                while (true){
                    logger.info("pool[executor],activeCount:{},poolSize:{}",ExecutorManager.executor.getActiveCount(),ExecutorManager.executor.getPoolSize());
                    logger.info("pool[handler],activeCount:{},poolSize:{}",ExecutorManager.handler.getActiveCount(),ExecutorManager.handler.getPoolSize());
                    try {
                        Thread.sleep(1000*5);
                    } catch (InterruptedException e) {

                    }
                }
        }).start();
    }
}
