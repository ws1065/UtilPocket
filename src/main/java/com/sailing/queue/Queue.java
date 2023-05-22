package com.sailing.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-10-25 17:22
 */
@Slf4j
public class Queue {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 1, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
    public static void main(String[] args) {
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        executor.submit(new Demo());
        log.debug("分析/请求/响应/发送/队列 长度 线程池大小 激活线程大小:"+ executor.getQueue().size()+"   "+ executor.getPoolSize()+"   "+ executor.getActiveCount()
        );
        System.out.println();
    }

}
class Demo extends Thread{
    @Override
    public void run() {
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}