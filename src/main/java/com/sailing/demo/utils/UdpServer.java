package com.demo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UdpServer {


    public static void main(String args[]) {

//从字符串中提取指定的字符串
        String s ="INVITE sip:71000000001320000008@7100000000 SIP/2.0\n" +
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

        String b1 =
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

        String regex = "[C][S][e][q].*[E]";
        String regex1 ="[2][0][0][ ][O][K]";

        Pattern pattern1 = Pattern.compile(regex);
        Pattern pattern2 = Pattern.compile(regex1);

        Matcher matcher = pattern1.matcher(s);
        Matcher matcher1 = pattern2.matcher(b1);
        boolean b = Pattern.matches(regex,s);

        if (matcher.find()) {
            String math = matcher.group(0);
            System.out.print(math);
                if("CSeq: 1 INVITE".equals(math)){
                    System.out.println("111111111111");
                }
        }
        if (matcher1.find()){
            String math1 = matcher1.group();
            System.out.println(math1);
            if("200 OK".equals(math1)){
                System.out.println("222222");
            }
        }


    }
}
