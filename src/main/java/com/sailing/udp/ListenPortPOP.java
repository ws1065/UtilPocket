package com.sailing.udp;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: VSCG
 * @description: 重新给视频点播请求赋予新的端口
 * @author: wangsw
 * @create: 2021-12-06 12:07
 */
@Slf4j
public class ListenPortPOP implements Runnable{
    public ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(20000);
    private int index;
    private int from;
    private int to;

    public ListenPortPOP(int from, int to) {
        this.from = from;
        this.index = from;
        this.to = to;

    }

    public static void main(String[] args) {
        ListenPortPOP target = new ListenPortPOP(40,90);
        for (int i = 0; i < 20000000; i++) {
            new Thread(()->{
                Integer item = target.queue.poll();
                while (item == null) item = target.queue.poll();
                System.out.print(" "+ item + " ");
            }).start();

        }
    }
    public  int getItem(){
        if (index >= to) {
            index = from;
            index+=2;
        }else {
            index+=2;
        }
        return index;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ListenPortPOP");
        while (true){
            try {
                queue.put(getItem());
            }catch (Exception e){
                log.error("ListenPortPOP error",e);
            }
        }
    }
}