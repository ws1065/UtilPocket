package com.sailing;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-10-27 16:25
 */
public class SimpleServer extends Thread {
    public static DatagramSocket sock;
    private int listenPort;

    public SimpleServer(int listenPort) {
        this.listenPort = listenPort;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            byte[] buf = new byte[20 * 1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                getSock().receive(packet);
                System.out.println();
            } catch (Exception e) {
            }
        }
    }

    private  DatagramSocket getSock() {
        if (sock == null) {
            initPortMonitor(listenPort);
        }
        return sock;
    }
    private  void initPortMonitor(int listenPort) {
        try {
            sock = new DatagramSocket(listenPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}