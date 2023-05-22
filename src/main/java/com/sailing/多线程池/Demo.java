package com.sailing.多线程池;

import java.util.concurrent.*;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-04-26 12:11
 */
public class Demo {

    public static  ExecutorService executor = new ReceiveThreadPool(0,Integer.MAX_VALUE,
                                      5,TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Future<?> submit = executor.submit(new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "执行了1");
            Thread.currentThread().setName("TESTESTE ");
            System.out.println(Thread.currentThread().getName() + "执行了2");
            System.out.println("hello");
        }));

        executor.submit(new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "执行了1");
            Thread.currentThread().setName("TESTESTE2 ");
            System.out.println(Thread.currentThread().getName() + "执行了2");
            System.out.println("hello");

            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        System.out.println(Thread.currentThread().getName() + "执行了3");
        System.out.println();
        Thread.sleep(Integer.MAX_VALUE);

    }
}