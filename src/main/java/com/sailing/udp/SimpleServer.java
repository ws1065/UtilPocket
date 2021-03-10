package com.sailing.udp;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-10-27 16:25
 */
@Slf4j
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
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
                getSock().receive(datagramPacket);
                InetAddress address = datagramPacket.getAddress();

                int length = datagramPacket.getLength();
                byte[] bytes = new byte[length];
                System.arraycopy(datagramPacket.getData(), 0, bytes, 0, length);

                log.debug("----------start------");
                log.debug(datagramPacket.getSocketAddress().toString());
                log.debug(new String(bytes));
                log.debug("----------end--------");

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