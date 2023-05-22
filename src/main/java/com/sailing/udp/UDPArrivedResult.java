package com.sailing.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sailing.udp.JNITest.ipToLong;


/**
 * 单光UDP服务回调
 */
public class UDPArrivedResult {
    private static Logger log = LoggerFactory.getLogger(UDPArrivedResult.class);
    public static byte[] key = null;
    public static long sumSendSize = 0L;// 总收到字节

    public static void errorPacket(String errorMsg) {
        log.error(errorMsg);
    }

    /**
     * UDP时接收端处理
     *
     * @param bf
     */
    public static void arriveResult(int sockfd, int ip, int port, byte[] bf) {
        //ip: 2130706433 port=
        log.info("UDP Receive:{}:{}:{}", sockfd, UDPSocket.gethostip(ip), UDPSocket.gethostport(port));
        int i2 = UDPSocket.respClient(sockfd, ip, port, "goof".getBytes(), "goof".getBytes().length);
        System.out.println("数据发送成功,发送数据为2"+i2);

        int getnetip = UDPSocket.getnetip("192.168.2.131");
        int getnetport = UDPSocket.getnetport(10001);
        int i3 = UDPSocket.respClient(sockfd, getnetip, getnetport, "wswresp".getBytes(), "wswresp".getBytes().length);
        System.out.println("数据发送成功,发送数据为3"+i3);
        int i4 = UDPSocket.respClient(JNITest.runServer, getnetip, getnetport, "re".getBytes(), "re".getBytes().length);
        System.out.println("数据发送成功,发送数据为4"+i4);
    }

    public static void main(String[] args) {
        System.out.println((int)ipToLong("192.168.91.131"));
    }

}