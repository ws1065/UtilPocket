package com.sailing.多线程池;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-04-26 12:21
 */
@Slf4j
public class ReceiveThreadPool extends ThreadPoolExecutor {
    public ReceiveThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                             java.util.concurrent.TimeUnit unit,
                             java.util.concurrent.BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        log.debug("afterExecute:{},{}",r,t);
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        super.terminated();
    }
}