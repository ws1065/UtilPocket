package com.sailing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-05-21 11:43
 */



public class SimpleMain {
    public static ThreadPoolExecutor sendProcess = new ThreadPoolExecutor(5, 6, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(10));
    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            sendProcess.submit(()->{
                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println();
        new Thread(()->Zimo.demo(1)).start();
        System.out.println("666");
        Zimo.demo(2);
        Thread.sleep(900000);
        String x = "/vscg/cluster/cb_stat/192.168.2.134-192.168.91.134";
        String[] split = x.split("/");
        Pattern compile = Pattern.compile("\\/vscg\\/cluster\\/(.*)\\/.*");
        Matcher matcher = compile.matcher(x);
        if (matcher.find()) {
            String group = matcher.group();
            System.out.println(group);
        }
        System.out.println(x);
    }
}