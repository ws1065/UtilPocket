package com.sailing.demo.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUDPClient {
    public static void main(String[] args) throws IOException {

                String a="1.2.344\tCentral\t0\n" +
                "Sep, 2017\n" +
                "1.2.343\tCentral\t0\n" +
                "Aug, 2017\n" +
                "1.2.342\tCentral\t0\n" +
                "Aug, 2017\n" +
                "1.2.340\tCentral\t0\n" +
                "Aug, 2017\n" +
                "1.2.339\tCentral\t0\n" +
                "Aug, 2017\n" +
                "1.2.336\tCentral\t0\n" +
                "Aug, 2017\n" +
                "1.2.331\tCentral\t0\n" +
                "Jun, 2017\n" +
                "1.2.329\tCentral\t0\n" +
                "Jun, 2017\n" +
                "1.2.327\tCentral\t1\n" +
                "Mar, 2017\n" +
                "1.2.324\tCentral\t2\n" +
                "Jan, 2017\n" +
                "1.2.323\tCentral\t0\n" +
                "Jan, 2017\n" +
                "1.2.322\tCentral\t2\n" +
                "Dec, 2016\n" +
                "1.2.321\tCentral\t1\n" +
                "Dec, 2016\n" +
                "1.2.320\tCentral\t0\n" +
                "Nov, 2016\n" +
                "1.2.318\tCentral\t2\n" +
                "Oct, 2016\n" +
                "1.2.317\tCentral\t0\n" +
                "Oct, 2016\n" +
                "1.2.314\tCentral\t0\n" +
                "Oct, 2016\n" +
                "1.2.310\tCentral\t0\n" +
                "Oct, 2016\n" +
                "1.2.309\tCentral\t0\n" +
                "Oct, 2016\n" +
                "1.2.308\tCentral\t1\n" +
                "Sep, 2016\n" +
                "1.2.307\tCentral\t12\n" +
                "Sep, 2016\n" +
                "1.2.306\tCentral\t0\n" +
                "Sep, 2016\n" +
                "1.2.305\tCentral\t12\n" +
                "Sep, 2016\n" +
                "1.2.303\tCentral\t0\n" +
                "Sep, 2016\n" +
                "1.2.302\tCentral\t0\n" +
                "Sep, 2016\n" +
                "1.2.301\tCentral\t12\n" +
                "Sep, 2016\n" +
                "1.2.300\tCentral\t8\n" +
                "Sep, 2016\n" +
                "1.2.299\tCentral\t0\n" +
                "Sep, 2016\n" +
                "1.2.298\tCentral\t0\n" +
                "Sep, 2016\n" +
                "1.2.296\tCentral\t0\n" +
                "Aug, 2016\n" +
                "1.2.295\tCentral\t12\n" +
                "Jul, 2016\n" +
                "1.2.293\tCentral\t11\n" +
                "Jun, 2016\n" +
                "1.2.292\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.290\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.289\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.288\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.287\tCentral\t12\n" +
                "May, 2016\n" +
                "1.2.286\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.285\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.284\tCentral\t0\n" +
                "May, 2016\n" +
                "1.2.283\tCentral\t13\n" +
                "May, 2016\n" +
                "1.2.281\tCentral\t12\n" +
                "Apr, 2016\n" +
                "1.2.280\tCentral\t0\n" +
                "Apr, 2016\n" +
                "1.2.279\tCentral\t0\n" +
                "Apr, 2016\n" +
                "1.2.277\tCentral\t18\n" +
                "Apr, 2016\n" +
                "1.2.274\tCentral\t0\n" +
                "Apr, 2016\n" +
                "1.2.273\tCentral\t0\n" +
                "Apr, 2016\n" +
                "1.2.272\tCentral\t12\n" +
                "Apr, 2016\n" +
                "1.2.271\tCentral\t9\n" +
                "Apr, 2016\n" +
                "1.2.270\tCentral\t0\n" +
                "Apr, 2016\n" +
                "1.2.269\tCentral\t12\n" +
                "Mar, 2016\n" +
                "1.2.268\tCentral\t1\n" +
                "Mar, 2016\n" +
                "1.2.267\tCentral\t12\n" +
                "Mar, 2016\n" +
                "1.2.266\tCentral\t1\n" +
                "Mar, 2016\n" +
                "1.2.265\tCentral\t12\n" +
                "Mar, 2016\n" +
                "1.2.264\tCentral\t0\n" +
                "Feb, 2016\n" +
                "1.2.263\tCentral\t12\n" +
                "Feb, 2016\n" +
                "1.2.262\tCentral\t0\n" +
                "Feb, 2016\n" +
                "1.2.261\tCentral\t0\n" +
                "Feb, 2016\n" +
                "1.2.259\tCentral\t26\n" +
                "Dec, 2015\n" +
                "1.2.258\tCentral\t10\n" +
                "Dec, 2015\n" +
                "1.2.257\tCentral\t15\n" +
                "Dec, 2015\n" +
                "1.2.256\tCentral\t6\n" +
                "Nov, 2015\n" +
                "1.2.255\tCentral\t0\n" +
                "Nov, 2015\n" +
                "1.2.252\tCentral\t1\n" +
                "Nov, 2015\n" +
                "1.2.250\tCentral\t0\n" +
                "Nov, 2015\n" +
                "1.2.249\tCentral\t0\n" +
                "Nov, 2015\n" +
                "1.2.247\tCentral\t0\n" +
                "Oct, 2015\n" +
                "1.2.246\tCentral\t0\n" +
                "Oct, 2015\n" +
                "1.2.245\tCentral\t1\n" +
                "Sep, 2015\n" +
                "1.2.244\tCentral\t3\n" +
                "Aug, 2015\n" +
                "1.2.243\tCentral\t0\n" +
                "Jul, 2015\n" +
                "1.2.242\tCentral\t10\n" +
                "Jul, 2015\n" +
                "1.2.241\tCentral\t1\n" +
                "Jun, 2015\n" +
                "1.2.240\tCentral\t0\n" +
                "Jun, 2015\n" +
                "1.2.234\tCentral\t0\n" +
                "Apr, 2015\n" +
                "1.2.233\tCentral\t1\n" +
                "Mar, 2015\n" +
                "1.2.232\tCentral\t9\n" +
                "Feb, 2015\n" +
                "1.2.231\tCentral\t0\n" +
                "Feb, 2015\n" +
                "1.2.230\tCentral\t6\n" +
                "Jan, 2015\n" +
                "1.2.228\tCentral\t6\n" +
                "Oct, 2014\n" +
                "1.2.227\tCentral\t0\n" +
                "Oct, 2014\n" +
                "1.2.226\tCentral\t0\n" +
                "Oct, 2014\n" +
                "1.2.225\tCentral\t0\n" +
                "Oct, 2014\n" +
                "1.2.224\tCentral\t6\n" +
                "Sep, 2014\n" +
                "1.2.222\tCentral\t6\n" +
                "Aug, 2014\n" +
                "1.2.221\tCentral\t0\n" +
                "Aug, 2014\n" +
                "1.2.220\tCentral\t6\n" +
                "Aug, 2014\n" +
                "1.2.219\tCentral\t0\n" +
                "Aug, 2014\n" +
                "1.2.218\tCentral\t0\n" +
                "Jul, 2014\n" +
                "1.2.217\tCentral\t0\n" +
                "Jul, 2014\n" +
                "1.2.216\tCentral\t0\n" +
                "Jul, 2014\n" +
                "1.2.214\tCentral\t0\n" +
                "Jul, 2014\n" +
                "1.2.213\tCentral\t0\n" +
                "Jul, 2014\n" +
                "1.2.212\tCentral\t0\n" +
                "Jul, 2014\n" +
                "1.2.211\tCentral\t7\n" +
                "Jul, 2014\n" +
                "1.2.210\tCentral\t13\n" +
                "Jun, 2014\n" +
                "1.2.209\tCentral\t0\n" +
                "Jun, 2014\n" +
                "1.2.206\tCentral\t0\n" +
                "Jun, 2014\n" +
                "1.2.203\tCentral\t0\n" +
                "Jun, 2014\n" +
                "1.2.170\tCentral\t1\n" +
                "Jun, 2012\n" +
                "1.2.170-NIO\tCentral\t16\n" +
                "Oct, 2012\n" +
                "1.2.169\tCentral\t16\n" +
                "Mar, 2012\n" +
                "1.2.168\tCentral\t0\n" +
                "Mar, 2012\n" +
                "1.2.167\tCentral\t3\n" +
                "Mar, 2012\n" +
                "1.2.166\tCentral\t1\n" +
                "Nov, 2011\n" +
                "1.2.165\tCentral\t0\n" +
                "Sep, 2011\n" +
                "1.2.164\tCentral\t16\n" +
                "Aug, 2011\n" +
                "1.2.163\tCentral\t1\n" +
                "Jul, 2011\n" +
                "1.2.162\tCentral\t7\n" +
                "Mar, 2011\n" +
                "1.2.161\tCentral\t9\n" +
                "Dec, 2010\n" +
                "1.2.160\tCentral\t4\n" +
                "Dec, 2010\n" +
                "1.2.159\tCentral\t1\n" +
                "Nov, 2010\n" +
                "1.2.158\tCentral\t8\n" +
                "Oct, 2010\n" +
                "1.2.157\tCentral\t6\n" +
                "Oct, 2010\n" +
                "1.2.154\tCentral\t1\n" +
                "Sep, 2010\n" +
                "1.2.153\tCentral\t13\n" +
                "Aug, 2010\n" +
                "1.2.151\tCentral\t9\n" +
                "Jul, 2010\n" +
                "1.2.150\tCentral\t4\n" +
                "Jun, 2010\n" +
                "1.2.149\tCentral\t9\n" +
                "May, 2010\n" +
                "1.2.148.10\tCentral\t0\n" +
                "Apr, 2011\n" +
                "1.2.148.9\tCentral\t0\n" +
                "Mar, 2011\n" +
                "1.2.148.8\tCentral\t0\n" +
                "Mar, 2011\n" +
                "1.2.148.6\tCentral\t0\n" +
                "Feb, 2011\n" +
                "1.2.148.5\tCentral\t0\n" +
                "Dec, 2010\n" +
                "1.2.148.4\tCentral\t0\n" +
                "Sep, 2010\n" +
                "1.2.148.3\tCentral\t0\n" +
                "Jul, 2010\n" +
                "1.2.148.2\tCentral\t0\n" +
                "Jul, 2010\n" +
                "1.2.148.1\tCentral\t0\n" +
                "May, 2010\n" +
                "1.2.148\tCentral\t0\n" +
                "May, 2010\n" +
                "1.2.146\tCentral\t9\n" +
                "Mar, 2010\n" +
                "1.2.142\tCentral\t1\n" +
                "Feb, 2010\n" +
                "1.2.139\tCentral\t15\n" +
                "Jan, 2010\n" +
                "1.2.136\tCentral\t0\n" +
                "Dec, 2009\n" +
                "1.2.135\tCentral\t14\n" +
                "Dec, 2009\n" +
                "1.2.123\tCentral\t5\n" +
                "Nov, 2009\n" +
                "1.2.122\tCentral\t0\n" +
                "Nov, 2009\n" +
                "1.2.108\tCentral\t19\n" +
                "Jun, 2009\n" +
                "1.2.100\tCentral\t13\n" +
                "May, 2009\n" +
                "1.2.96\tCentral\t9\n" +
                "Mar, 2009\n" +
                "1.2.90\tCentral\t13\n" +
                "Jan, 2009\n" +
                "1.2.89\tCentral\t11\n" +
                "Dec, 2008\n" +
                "1.2.86\tCentral\t11\n" +
                "Nov, 2008\n" +
                "1.2.85\tCentral\t7\n" +
                "Oct, 2008\n" +
                "1.2.83\tCentral\t9\n" +
                "Sep, 2008\n" +
                "1.2.76\tCentral\t10\n" +
                "Jul, 2008\n" +
                "1.2.73\tCentral\t1\n" +
                "Jun, 2008\n" +
                "1.2.1.4\tCentral\t0\n" +
                "May, 2010\n" +
                "1.2.1.3\tCentral\t0\n" +
                "May, 2010\n" +
                "1.2.1\tCentral\t9\n" +
                "Mar, 2008\n" +
                "1.2\tCentral\t1\n" +
                "May, 2010\n" +
                "1.2-2007-07-25\tCentral\t1\n" +
                "May, 2010\n" +
                "\n" ;

        String[] split = a.split("\n");
        for (int i = 0; i < split.length; i++) {
            if (i%2 == 0){
                String b = "        <dependency>\n" +
                        "            <groupId>javax.sip</groupId>\n" +
                        "            <artifactId>jain-sip-ri</artifactId>\n" +
                        "            <version>"+split[i].split("\t")[0]+"</version>\n" +
                        "        </dependency>";
                System.out.println(b);
            }
        }
        String l1 = "我们的家";
        System.out.println(l1.length());
        byte[] gb2312s = l1.getBytes("gbk");
        System.out.println(new String(gb2312s).length());
        try {
            DatagramSocket client = new DatagramSocket();
            System.out.println("---------------------------客户端已创建-------------------------------------");
            //2.准备要发送的数据 字节数组
            /**
             * 向服务端发送 INVITE(第一次发送信息)
             */

            //创建端口数量
            int sl = 2 ;
            //设置端口初始值
            int portcsz = 6000;
            //端口
            int port =portcsz;
            //创建端口范围
            int portsl = port+sl;
            //上级平台ip
            String sjptip = "127.0.0.1";
            //下级平台ip
            String xjptip = "127.0.0.1" ;
            while (port<=portsl){
            System.out.println("---------------------------向服务端发送 INVITE(第一次发送信息)-------------------");
            String msg =
                    "INVITE sip:71000000001320000008@7100000000 SIP/2.0\n" +
                    "Call-ID: 1201421830150f34a03a8365b57c8ff8@0.0.0.0\n" +
                    "CSeq: 1 INVITE\n" +
                    "From: <sip:70000000002000000016@7000000000>;tag=65461811_53173353_02c76721-e5c8-4a67-8628-a8361c34d6f5\n" +
                    "To: <sip:71000000001320000008@7100000000>\n" +
                    "Max-Forwards: 70\n" +
                    "Contact: \"70000000002000000016\" <sip:"+xjptip+":5060>\n" +
                    "Subject: 71000000001320000008:0-4-0,70000000002000000016:1\n" +
                    "Content-Type: application/sdp\n" +
                    "Route: <sip:71000000001320000008@"+sjptip+":5060;lr>\n" +
                    "Via: SIP/2.0/UDP "+xjptip+":5060;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72694167158299\n" +
                    "Content-Length: 221\n" +
                    "\n" +
                    "v=0\n" +
                    "o=71000000002020000020 0 0 IN IP4 "+sjptip+"\n" +
                    "s=Play\n" +
                    "c=IN IP4 "+sjptip+"\n" +
                    "t=0 0\n" +
                    "m=video"+ port +"RTP/AVP 96 98 97\n" +
                    "a=recvonly\n" +
                    "a=rtpmap:96 PS/90000\n" +
                    "a=rtpmap:97 MPEG4/90000\n" +
                    "a=rtpmap:98 H264/90000\n" +
                    "f=v/2/4///a///";
            byte[] data = msg.getBytes("utf-8");

            //3.给数据打包 并指定发送的目的地和端口  DatagramPacket(byte buf[], int length, InetAddress address, int port)
            InetAddress address = InetAddress.getByName("127.0.0.1");
            DatagramPacket packet = new DatagramPacket(data, data.length, address, 19001);

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
                byte[]  data1 = datagramPacket1.getData();
            //将字节组转为字符串
            String receiveifon = new String(data1);

            System.out.println("---------------------------服务端返回信息：---------------------------------------");
                System.out.println(receiveifon);

            /**
             *客户端收到服务端返回的200 OK 返回服务端ACK（客户端第二次发送信息给服务端）
             */

            //解析服务端返回的数据200 OK
            String regex = "[2][0][0][ ][O][K]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(receiveifon);

            if (matcher.find()) {
                String math = matcher.group();
                //System.out.println(math);
                //String regex1 = "[2][0][0][ ][O][K]";
                //boolean b1 = Pattern.matches(regex1,math);
                // System.out.println(b1);
                if ("200 OK".equals(math)) {
                    String ack =
                            "ACK sip:71000000001320000008@172.20.36.100:5060 SIP/2.0\n" +
                                    "Call-ID: 1201421830150f34a03a8365b57c8ff8@0.0.0.0\n" +
                                    "CSeq: 1 ACK\n" +
                                    "From: <sip:70000000002000000016@7000000000>;tag=65461811_53173353_02c76721-e5c8-4a67-8628-a8361c34d6f5\n" +
                                    "To: <sip:71000000001320000008@7100000000>;tag=f1646154\n" +
                                    "Max-Forwards: 70\n" +
                                    "Via: SIP/2.0/UDP 172.20.32.16:5060;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72694188277400\n" +
                                    "Content-Length: 0";

                    byte[] bytes1 = ack.getBytes();
                    SocketAddress address1 = packet.getSocketAddress();
                    DatagramPacket datagramPacket2 = new DatagramPacket(bytes1, bytes1.length, address1);
                    //发送信息
                    client.send(datagramPacket2);

                    System.out.println("-------------------------------------启动iperf------------------------------------");

                     LinuxClient linuxClient = new LinuxClient("172.20.52.117", 22, "root", "sailing");
                     linuxClient.commitAndPrint("iperf -s -p"+port+"-u -t 15");
                     linuxClient.closeConnection();

                }

               // System.out.println("-------------------------------------启动iperf------------------------------------");
               // LinuxClient linuxClient = new LinuxClient("172.20.52.117", 22, "root", "sailing");
               // linuxClient.commitAndPrint("iperf -s -p"+port+"-u -t 15");
                // linuxClient.closeConnection();

            }

                if(port==portsl){
                    System.out.println("客户端向服务端发送BYE,此时端口为:"+port);
                    int i = portcsz ;
                    while ( i <=portsl){
                        System.out.println("Iperf关闭之后，客户端向服务端发送BYE,关闭端口:"+i);
                        String bye = "BYE sip:71000000001320000008@172.20.36.100:5060（下级平台IP） SIP/2.0\n" +
                                "CSeq: 2 BYE\n" +
                                "From: <sip:70000000002000000016@7000000000>;tag=58279595_53173353_02c76721-e5c8-4a67-8628-a8361c34d6f5\n" +
                                "To: <sip:71000000001320000008@7100000000>;tag=792be24c\n" +
                                "Call-ID: 5edc69244685d98df9d4629a4b5a5315@0.0.0.0\n" +
                                "Max-Forwards: 70\n" +
                                "Via: SIP/2.0/UDP 172.20.32.16:5060;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72314204033816\n" +
                                "Content-Length: 0";

                        byte[] bytes2 = bye.getBytes();
                        SocketAddress address2 = packet.getSocketAddress();
                        DatagramPacket datagramPacket3 = new DatagramPacket(bytes2, bytes2.length, address2);
                        //发送信息
                        client.send(datagramPacket3);

                        /**
                         * 客户端接收服务端接受到BYE之后的 200 OK
                         */
                        System.out.println("---------------客户端接收服务端接受到BYE之后的 200 OK-------------------------------");
                        //从服务端接收信息
                        //创建字节组存放信息
                        byte[] bytes1 = new byte[1024];
                        //创建数据包，接收数组数据包
                        DatagramPacket datagramPacket2 = new DatagramPacket(bytes1,bytes1.length);
                        client.receive(datagramPacket2);

                        //解析数据，读取服务器发送的信息
                        byte[]  data2 = datagramPacket2.getData();
                        //将字节组转为字符串
                        String receiveifon1 = new String(data2);

                        System.out.println("服务端返回信息:");
                        System.out.println(receiveifon1);

                        i++;
                    }
                }else {
                    System.out.println("不进行客户端向服务端发送BYE，此时端口为:"+port);
                }

                port+=1;

            }
            //5.释放
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
