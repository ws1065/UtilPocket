package com.demo.utils;

import com.sailing.demo.utils.LinuxClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveDemo {

    public static void main(String[] args) throws IOException {
        try {
            //创建接收端的Socket服务对象，并且指定端口号
            DatagramSocket ds = new DatagramSocket(19001);
            System.out.println("------------------------------服务端已开启-------------------------------------------");

            //创建端口数量
            int sl = 2 ;
            //设置端口初始值
            int portcsz = 6000;
            //端口
            int port =portcsz;
            //创建端口数量
            int portsl = port+sl;
            //上级平台ip
            String sjptip = "127.0.0.1";
            //下级平台ip
            String xjptip = "127.0.0.1" ;
            while (port<=portsl) {
                /**
                 * 服务端接收客户端INVITE（第一次接收信息）
                 */
                System.out.println("------------------------------服务端接收客户端INVITE----------------------------------");
                //创建一个数据包，用于接收数据
                byte[] bys = new byte[1024];
                DatagramPacket dp = new DatagramPacket(bys, bys.length);

                //接收数据
                ds.receive(dp);

                //解析数据
                //获取ip地址
                String ip = dp.getAddress().getHostAddress();

                //获取数据
                String data = new String(dp.getData(), 0, dp.getLength());
                System.out.println(data);


                /**
                 * 正则表达式解析INVITE
                 */

                Thread.sleep(2000);
                System.out.println("------------------------正则表达式解析INVITE------------------------------------------------");
                //String invite = "[I][N][V][I][T][E]";
                String invite = "[C][S][e][q].*[E]";
                //匹配出INVITE
                Pattern pattern = Pattern.compile(invite);
                Matcher matcher = pattern.matcher(data);

                if (matcher.find()) {
                    String math = matcher.group();
                    System.out.println(math);
                    //String extractinvite = "CSeq: 1 INVITE";
                    //String invite1 = ".*[I][N][V][I][T][E].?";
                    //String invite1 = "[C][S][e][q].*[E]";
                    //boolean extractinvite = Pattern.matches(invite1,math);
                    //System.out.println(extractinvite);
                    /**
                     * 服务端发送数据200 OK给客户端(服务器第一次发送信息给客户端)
                     */
                    //服务端收到数据之后向客户端发送信息
                    if ("CSeq: 1 INVITE".equals(math)) {
                        System.out.println("------------------------ 服务端发送数据200 OK给客户端------------------------------------------------");
                        String INVITE =
                                "SIP/2.0 200 OK\n" +
                                        "Via: SIP/2.0/UDP " + xjptip + ":5060;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72694167158299\n" +
                                        "Contact: <sip:71000000001320000008@" + xjptip + ":5060>\n" +
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
                        //System.out.println(INVITE);
                        byte[] bytes = INVITE.getBytes();
                        SocketAddress address = dp.getSocketAddress();
                        DatagramPacket datagramPacket1 = new DatagramPacket(bytes, bytes.length, address);
                        //发送信息
                        ds.send(datagramPacket1);
                    }

                    /**
                     * 服务端接收客户端发送的ACK(服务端第二次接收客户端数据)
                     */
                    //创建一个数据包，用于接收数据
                    byte[] bys1 = new byte[1024];

                    DatagramPacket dp1 = new DatagramPacket(bys1, bys1.length);

                    //接收数据
                    ds.receive(dp1);
                    System.out.println("------------------------ 服务端接收客户端发送的ACK------------------------------------------------");
                    //获取数据
                    String data1 = new String(dp1.getData(), 0, dp1.getLength());
                    System.out.println(data1);

                    /**
                     * 正则表达式解析ACK
                     */
                    String regex = "[A][C][K]";

                    Pattern pattern1 = Pattern.compile(regex);

                    Matcher matcher1 = pattern1.matcher(data1);

                    if(matcher1.find()){
                        System.out.println("-------------------------------------启动iperf------------------------------------");

                        LinuxClient linuxClient = new LinuxClient("172.20.54.118", 22, "root", "sailing");

                        linuxClient.commitAndPrint("iperf -c 172.20.52.117 -p" +port+ "-u -t 15   ");

                        linuxClient.closeConnection();
                    }

                }

                if(port==portsl) {
                    System.out.println("准备接受BYE,此时端口为:"+port);
                    int i = portcsz ;
                    while ( i <=portsl) {
                        System.out.println("服务端准备接收客户端BYE:"+i);
                        //创建一个数据包，用于接收数据
                        byte[] bys2 = new byte[1024];
                        DatagramPacket dp1 = new DatagramPacket(bys2, bys2.length);

                        //接收数据
                        ds.receive(dp1);

                        //解析数据
                        //获取ip地址
                        String ip1 = dp1.getAddress().getHostAddress();

                        //获取数据
                        String data1 = new String(dp1.getData(), 0, dp1.getLength());

                        System.out.println(data1);



                        System.out.println("服务端发送客户端200 OK");

                        String twook ="SIP/2.0 200 OK\n" +
                                "Via: SIP/2.0/UDP 172.20.32.16:5060（上级平台IP）;branch=z9hG4bK02c76721-e5c8-4a67-8628-a8361c34d6f5_53173353_72314204033816\n" +
                                "Contact: <sip:71000000001320000008@172.20.36.100:5060>\n" +
                                "To: <sip:71000000001320000008@7100000000>;tag=792be24c\n" +
                                "From: <sip:70000000002000000016@7000000000>;tag=58279595_53173353_02c76721-e5c8-4a67-8628-a8361c34d6f5\n" +
                                "Call-ID: 5edc69244685d98df9d4629a4b5a5315@0.0.0.0\n" +
                                "CSeq: 2 BYE\n" +
                                "User-Agent: SAILING\n" +
                                "Content-Length: 0";

                        byte[] bytes = twook.getBytes();
                        SocketAddress address = dp.getSocketAddress();
                        DatagramPacket datagramPacket1 = new DatagramPacket(bytes, bytes.length, address);
                        //发送信息
                        ds.send(datagramPacket1);


                        i++;







                    }
                }else {
                    System.out.println("不进行接受BYE,此时端口为:"+port);
                }
                port+=1;
            }


        }catch (InterruptedException e){
            e.printStackTrace();
        }

        }

        //释放资源(服务器一般永远是开着的）
        //ds.close();
    }

