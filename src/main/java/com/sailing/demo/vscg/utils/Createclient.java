package com.demo.vscg.utils;

import org.omg.PortableInterceptor.INACTIVE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class Createclient {

    DatagramSocket client = new DatagramSocket();

    public Createclient() throws SocketException {
        //1.创建客户端+端口
        System.out.println("---------------------------客户端已创建-------------------------------------");

    }

    /**
     * 向服务端发送 INVITE(第一次发送信息)
     */

    public void INVITE() throws IOException {

        System.out.println("---------------------------向服务端发送 INVITE(第一次发送信息)-------------------");
        String msg = "INVITE sip:71000000001320000008@7100000000 SIP/2.0\n" +
                "Call-ID: 1201421830150f34a03a8365b57c8ff8@0.0.0.0\n" +
                "CSeq: 1 INVITE\n" +
                "From: <sip:70000000002000000016@7000000000>;tag=65461811_53173353_02c76721-e5c8-4a67-8628-a8361c34d6f5\n" +
                "To: <sip:71000000001320000008@7100000000>\n" +
                "Max-Forwards: 70\n" +
                "Contact: \"70000000002000000016\" <sip:172.20.32.16:5060>\n" +
                "Subject: 71000000001320000008:0-4-0,70000000002000000016:1\n" +
                "Content-Type: application/sdp\n" +
                "Route: <sip:71000000001320000008@172.20.36.100:5060;lr>\n" +
                "Via: SIP/2.0/UDP 127.0.0.1:5060;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72694167158299\n" +
                "Content-Length: 221\n" +
                "\n" +
                "v=0\n" +
                "o=71000000002020000020 0 0 IN IP4 127.0.0.1\n" +
                "s=Play\n" +
                "c=IN IP4 127.0.0.1\n" +
                "t=0 0\n" +
                "m=video 6000 RTP/AVP 96 98 97\n" +
                "a=recvonly\n" +
                "a=rtpmap:96 PS/90000\n" +
                "a=rtpmap:97 MPEG4/90000\n" +
                "a=rtpmap:98 H264/90000\n" +
                "f=v/2/4///a///";
        byte[] data = msg.getBytes("utf-8");


        //3.给数据打包 并指定发送的目的地和端口  DatagramPacket(byte buf[], int length, InetAddress address, int port)
        InetAddress address = InetAddress.getByName("127.0.0.1");
        DatagramPacket packet = new DatagramPacket(data, data.length, address, 10010);
        //4.发送
        client.send(packet);
        System.out.println("---------------向服务端发送 INVITE(第一次发送信息)发送成功-------------------------------");
    }



}
