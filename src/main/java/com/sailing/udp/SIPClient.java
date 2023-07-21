package com.sailing.udp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-12-15 10:57
 */
@Slf4j
public class SIPClient {
    private static InetSocketAddress address;
    private static DatagramSocket datagramSocket;
    public static AtomicInteger port = new AtomicInteger(1);



    public static String run() {
        String rawData = "REGISTER sip:34020000001310000@172.20.52.123:5060 SIP/2.0\r\n" +
                "Via: SIP/2.0/UDP 172.20.52.130:15060;rport;branch=z9hG4bK321142639\r\n" +
                "From: <sip:34020000002000052130@3402000000>;tag=913142639\r\n" +
                "To: <sip:34020000001310000@172.20.52.123:5060>\r\n" +
                "Call-ID: "+ UUID.randomUUID().toString()+"\r\n" +
                "CSeq: 2108 REGISTER\r\n" +
                "Content-Type: APPLICATION/SDP\r\n" +
                "Contact: <sip:34020000002000052130@172.20.52.130:15060>\r\n" +
                "Max-Forwards: 70\r\n" +
                "User-Agent: LiveGBS v220328\r\n" +
                "Subject: 34020000001310000004:0200000004,34020000002000052130:0\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        return rawData;
    }

    public static void main(String[] args) {
        run("127.0.0.1",5060,run());
    }
    public static void run(String dstHost, int dstPort,String rawData) {
        try {
            address = new InetSocketAddress(dstHost, dstPort);
            InetSocketAddress localAddress = new InetSocketAddress("127.0.0.1", 9090);
            datagramSocket = new DatagramSocket(localAddress);

            DatagramPacket datagramPacket = null;

            byte[] bytes = rawData.getBytes();
            datagramPacket = new DatagramPacket(bytes, bytes.length, address);
            log.info("发送数据：{}",rawData);
            //发送
            datagramSocket.send(datagramPacket);
//            Thread.sleep(1000);
//            byte[] buf = new byte[20 * 1024];
//            DatagramPacket datagramPacket1 = new DatagramPacket(buf, buf.length);
////            //使用同一个socket接收
//            datagramSocket.receive(datagramPacket1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}