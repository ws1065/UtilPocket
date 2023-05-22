package com.sailing.demo.vscg;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SipServer {

    public static void main(String[] args) throws IOException {
        try {
            //创建接收端的Socket服务对象，并且指定端口号
            DatagramSocket ds = new DatagramSocket(10010);
            System.out.println("服务端已开启");

            /**
             * 服务端接收客户端INVITE（第一次接收信息）
             */
            //创建一个数据包，用于接收数据
            byte[] bys = new byte[1024];
            DatagramPacket dp = new DatagramPacket(bys, bys.length);

            //接收数据
            ds.receive(dp);

            //解析数据
            //获取ip地址
            String ip = dp.getAddress().getHostAddress();

            //获取数据
            String data = new String(dp.getData(),0,dp.getLength());
            System.out.println(data);


            /**
             * 正则表达式解析CSeq: 1 INVITE
             */

            Thread.sleep(2000);
            System.out.println("匹配获取数据内容");
            //String invite = "[I][N][V][I][T][E]";
            String invite ="[C][S][e][q].*[E]";
            //匹配出CSeq: 1 INVITE
            Pattern pattern = Pattern.compile(invite);
            Matcher matcher = pattern.matcher(data);


            while (matcher.find()){
                String math = matcher.group();
                System.out.println(math);

                String extractinvite = "CSeq: 1 INVITE";

                //String invite1 = ".*[I][N][V][I][T][E].?";
                //String invite1 = "[C][S][e][q].*[E]";
                //boolean extractinvite = Pattern.matches(invite1,math);
                //System.out.println(extractinvite);
                /**
                 * 服务端发送数据200 OK给客户端(服务器第一次发送信息给客户端)
                 */

                //服务端收到数据之后向客户端发送信息
                if(extractinvite.equals(math)) {
                    String sendInfo =
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
                    byte[] bytes = sendInfo.getBytes();
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

                //获取数据
                String data1 = new String(dp1.getData(),0,dp1.getLength());
                System.out.println(data1);

            }

        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    //释放资源(服务器一般永远是开着的）
    //ds.close();
}
