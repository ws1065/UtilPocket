package com.sailing.udpV6.udp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: udpProxyDemo
 * @description: 发送数据
 * @author: wangsw
 * @create: 2020-05-28 20:52
 */
@Slf4j
public class SendDataDemo extends Thread {

    public static ArrayBlockingQueue<byte[]> sendQueue = new ArrayBlockingQueue<>(2000);

    private DatagramSocket socket;
    private String destIP;
    private int port;
    private static int i = 0;

    public SendDataDemo(String destIP, int port) {
        this.destIP = destIP;
        this.port = port;
    }

    @Override
    public void run() {
        while (true){
            try {
                byte[] take = sendQueue.take();
                if (sendQueue.size() >=1000) {
                    log.warn("发送队列过慢..[{}]",sendQueue.size());
                }
                take =  process(take);
                send(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(byte[] bytes) throws InterruptedException, IOException {
        //传入0表示让操作系统分配一个端口号
        DatagramSocket socket = getSocket();
        //指定包要发送的目的地
        DatagramPacket request = new DatagramPacket(bytes, bytes.length,
                InetAddress.getByName(destIP), port);
        socket.send(request);
    }

    //处理包内容
    //重点处理类
    private byte[] process(byte[] take) {
        //不是很精确的改一下查看效果
        i++;
        //隔位更改
        if (false) {
            if (i % 100 == 1) {
                log.info("{}:{}", take.length, take[take.length - 1]);
                //更改数据包内容
                take[take.length - 1]--;
                //更改整个数据包
                log.info("_________{}:{}", take.length, take[take.length - 1]);
            }
            //插白帧
        }else {
            if (i % 1000 == 1) {
                log.info("{}:{}", take.length, take[take.length - 1]);
                //更改数据包内容
                for (int j = 13; j < take.length; j++) {
                    take[j] = 0;
                }
                take[take.length - 1]--;
                //更改整个数据包
                log.info("_________{}:{}", take.length, take[take.length - 1]);
            }
        }
        return take;

    }
    /**
     * @Description: 获得当前发送的datagramsocket
     * @Param: 无
     * @return: Socket
     * @Author: wangsw
     * @date: 2019年7月3日
     */
    public DatagramSocket getSocket() {
        try {
            if (socket == null) {
                socket = new DatagramSocket(0);
            }
        } catch (Exception e) {
            log.error("Error found: ", e);
        }
        return socket;
    }
}