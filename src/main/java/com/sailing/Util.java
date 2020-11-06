package com.sailing;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @program: vscg-platform
 * @description: 工具类
 * @author: wangsw
 * @create: 2020-06-27 21:38
 */
public class Util {


    public static void udpSend(String host, String port, String req)  {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(req.getBytes(),
                    req.getBytes().length,
                    InetAddress.getByName(host),
                    Integer.valueOf(port));
            getSock().send(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DatagramSocket sock;
    public static DatagramSocket getSock() {
        try {
            if (sock == null) {
                sock = new DatagramSocket();
            }
            return sock;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}