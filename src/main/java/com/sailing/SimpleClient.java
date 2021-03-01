package com.sailing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-12-15 10:57
 */
public class SimpleClient {
    private InetSocketAddress address;
    private DatagramSocket datagramSocket;

    public static void main(String[] args) {
        new SimpleClient().run();
    }
    public void run() {
        try {
            address = new InetSocketAddress("127.0.0.1", 5060);
            datagramSocket = new DatagramSocket();

            DatagramPacket datagramPacket = null;
            datagramPacket = new DatagramPacket("message".getBytes(), "message".length(), address);
            datagramSocket.send(datagramPacket);
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}