package com.sailing;

import lombok.Data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-10-27 16:25
 */
@Data
public class SimpleServer extends Thread {
    public static DatagramSocket sock;
    private SysProperties sysProperties;

    public SimpleServer(SysProperties sysProperties) {
        this.sysProperties =sysProperties;

        initPortMonitor();
        this.setDaemon(true);
        new Thread(()->{
            while (true);
        }).start();
        this.start();
    }
    public  DatagramSocket getSock() {
        if (sock == null) {
            initPortMonitor();
        }
        return sock;
    }
    @Override
    public void run() {
        while (true) {
            byte[] buf = new byte[20 * 1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                getSock().receive(packet);
            } catch (Exception e) {
            }
        }
    }
    private  void initPortMonitor() {
        String selfIPAndPortToLanStr = sysProperties.getSelfIpAndPortToLan();
        String selfIPAndPortToWanStr = sysProperties.getSelfIpAndPortToWan();
        String[] selfIPAndPortToWan = selfIPAndPortToWanStr.split(":");
        String[] selfIPAndPortToLan = selfIPAndPortToLanStr.split(":");
        String myHostIPToLan = selfIPAndPortToLan[0];
        String myHostIPToWan = selfIPAndPortToWan[0];
        int myPort = Integer.parseInt(selfIPAndPortToLan[1]);
        //增加多一个端口用于给上级发送消息的本地端口
        try {
            sock = new DatagramSocket(myPort);
        } catch (Exception e) {

        }
    }
    //----------业务参数-start-------------------

}