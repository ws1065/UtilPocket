package com.sailing.udp;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @program: udpProxyDemo
 * @description: 接收udp
 * @author: wangsw
 * @create: 2020-05-28 20:44
 */
@Slf4j
public class ReceiveDemo implements Runnable{
    private DatagramSocket socket;
    private int port;
    public ReceiveDemo(int port) {
        try {
            this.port = port;
            if (socket == null) {
                socket = getSocket(port);
            }
        } catch (SocketException e) {
            log.error("error",e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (socket == null) {
                    socket = getSocket(port);
                }
                // 定义接收网络数据的字节数组
                byte[] inBuff = new byte[2048];
                // 以指定字节数组创建准备接收数据的DatagramPacket对象
                DatagramPacket packet = new DatagramPacket(inBuff , inBuff.length);
                socket.receive(packet);
                byte[] a = new byte[packet.getLength()];
                System.arraycopy(inBuff,0,a,0,packet.getLength());
                SendDataDemo.sendQueue.put(a);
            } catch (Exception e) {
                log.error("Error found: ", e);
            }
        }
    }
    private DatagramSocket getSocket(int port) throws SocketException {
        if (socket == null) {
            socket = new DatagramSocket(port);
            socket.setReceiveBufferSize(3*1024*1024);
        }
        return socket;
    }
}