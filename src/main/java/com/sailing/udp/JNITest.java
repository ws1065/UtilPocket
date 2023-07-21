package com.sailing.udp;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-11-29 14:07
 */
public class JNITest {

    public static int runServer;

    /**
         * ip地址转成long型数字
         * 将IP地址转化成整数的方法如下：
         * 1、通过String的split方法按.分隔得到4个长度的数组
         * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
         */
        public static int ipToLong(String strIp) {
            String[]ip = strIp.split("\\.");
            return  (int)((Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]));
        }

        public static String longToIP(int intIp) {
            long longIp = Long.parseLong(Integer.toUnsignedString(intIp));
            StringBuffer sb = new StringBuffer("");
            // 直接右移24位
            sb.append((longIp >>> 24));
            sb.append(".");
            // 将高8位置0，然后右移16位
            sb.append(((longIp & 0x00FFFFFF) >>> 16));
            sb.append(".");
            // 将高16位置0，然后右移8位
            sb.append(((longIp & 0x0000FFFF) >>> 8));
            sb.append(".");
            // 将高24位置0
            sb.append((longIp & 0x000000FF));
            return sb.toString();
        }

    public static void main(String[] args) {


        int getnetip = UDPSocket.getnetip("192.168.2.131");
        int getnetport = UDPSocket.getnetport(10001);
        System.out.println(getnetip);// == -2096977728
        System.out.println(getnetport);// == 4391

        JNITest.runServer = UDPSocket.runServer(9988, 1500, 20000);
           // int i2 = UDPSocket.respClient(runServer, getnetip, getnetport, "wswresp".getBytes(), "wswresp".getBytes().length);


    }

}