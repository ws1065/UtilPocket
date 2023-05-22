package com.sailing.udp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-10-27 16:25
 */
@Slf4j
public class Demo  {

    public static DatagramSocket sock;

    public void send(byte[] bytes,String peerAddress,int peerPort) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(bytes,
                bytes.length,
                InetAddress.getByName(peerAddress),
                peerPort);
        sock.send(datagramPacket);
    }
    public void receive(DatagramSocket sock) throws IOException {
        byte[] buf = new byte[20 * 1024];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        log.info("初始化：{}",datagramPacket);
        sock.receive(datagramPacket);

    }

    private  DatagramSocket init(int listenPort) throws SocketException {
        sock = new DatagramSocket(0);
        sock.bind(new InetSocketAddress(listenPort));
        return sock;
    }
}