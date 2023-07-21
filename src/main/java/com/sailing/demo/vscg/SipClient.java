package com.sailing.demo.vscg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SipClient {

    public static void main(String[] args) throws IOException {

        try {
            //1.创建客户端+端口
            DatagramSocket client = new DatagramSocket();
            System.out.println("---------------------------客户端已创建-------------------------------------");

            //2.准备要发送的数据 字节数组
            /**
             * 向服务端发送 INVITE(第一次发送信息)
             */

            System.out.println("---------------------------向服务端发送 INVITE(第一次发送信息)-------------------");
            //发送的数据msg
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
            System.out.println("客户端发送的数据"+msg);


            byte[] data = msg.getBytes("utf-8");

            //3.给数据打包 并指定发送的目的地和端口  DatagramPacket(byte buf[], int length, InetAddress address, int port)
            InetAddress address = InetAddress.getByName("127.0.0.1");
            DatagramPacket packet = new DatagramPacket(data, data.length, address, 10010);

            //4.发送
            client.send(packet);


            System.out.println("---------------向服务端发送 INVITE(第一次发送信息)发送成功-------------------------------");



            /**
             * 客户端接收服务端200 OK（客户端第一次接收服务端信息）
             */
            System.out.println("---------------客户端接收服务端200 OK（客户端第一次接收服务端信息）-------------------------------");
            //从服务端接收信息
            //创建字节组存放信息
            byte[] bytes = new byte[1024];
            //创建数据包，接收数组数据包
            DatagramPacket datagramPacket1 = new DatagramPacket(bytes,bytes.length);
            client.receive(datagramPacket1);
            //解析数据，读取服务器发送的信息
            byte[] data1 = datagramPacket1.getData();
            //将字节组转为字符串
            String receiveifon = new String(data1);

            System.out.println("服务端返回信息："+receiveifon);


            /**
             *客户端收到服务端返回的200 OK 返回服务端ACK（客户端第二次发送信息给服务端）
             */

            //解析服务端返回的数据200 OK
            String regex = "[2][0][0][ ][O][K]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(receiveifon);

            while (matcher.find()){
                String math = matcher.group();
                System.out.println(math);

                String regex1 = "[2][0][0][ ][O][K]";

                boolean b1 = Pattern.matches(regex1,math);
                System.out.println(b1);

                if(b1==true){
                    String twohundredok =
                            "SIP/2.0 200 OK\n" +
                                    "Via: SIP/2.0/UDP 172.20.32.16:5060;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72694167158299\n" +
                                    "Contact: <sip:71000000001320000008@172.20.36.100:5060>\n" +
                                    "To: <sip:71000000001320000008@7100000000>;tag=f1646154\n" +
                                    "From: <sip:70000000002000000016@7000000000>;tag=65461811_53173353_02c76721-e5c8-4a67-8628-a8361c34d6f5\n" +
                                    "Call-ID: 1201421830150f34a03a8365b57c8ff8@0.0.0.0\n" +
                                    "CSeq: 1 INVITE\n" +
                                    "Session-Expires: 1800;refresher=uas\n" +
                                    "Min-SE: 90\n" +
                                    "Accept-Language: en\n" +
                                    "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, NOTIFY, SUBSCRIBE, INFO, MESSAGE\n" +
                                    "Content-Type: application/sdp\n" +
                                    "Supported: timer\n" +
                                    "User-Agent: SAILING\n" +
                                    "Content-Length: 213\n" +
                                    "\n" +
                                    "v=0\n" +
                                    "o=71000000002000000100 0 0 IN IP4 172.20.32.64\n" +
                                    "s=Play\n" +
                                    "c=IN IP4 172.20.32.64\n" +
                                    "t=0 0\n" +
                                    "m=video 6970 RTP/AVP 96\n" +
                                    "a=sendonly\n" +
                                    "a=rtpmap:96 PS/90000\n" +
                                    "a=rtpmap:97 MPEG4/90000\n" +
                                    "a=rtpmap:98 H264/90000\n" +
                                    "y=0132000008";

                    byte[] bytes1 = twohundredok.getBytes();
                    SocketAddress address1 = packet.getSocketAddress();
                    DatagramPacket datagramPacket2 = new DatagramPacket(bytes1, bytes1.length, address1);

                    //发送信息
                    client.send(datagramPacket2);
                }

            }
            //5.释放
            client.close();


        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
