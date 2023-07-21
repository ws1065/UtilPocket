package com.sailing.udp;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-10-27 16:25
 */
@Slf4j
public class SimpleServer extends Thread {
    public static DatagramSocket sock;
    private int listenPort;

    public static void main(String[] args)  {
        try {
            new Thread(new SimpleServer(5060)).start();
            //new Thread(new SimpleServer(9999)).start();

        }catch (Exception e){
            log.error("error",e);
        }
    }
    public SimpleServer(int listenPort) {
        this.listenPort = listenPort;
        Thread.currentThread().setName("daemon-"+ new Random().nextInt(100));
    }

    @Override
    public void run() {
        while (true) {
            byte[] buf = new byte[20 * 1024];
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
                getSock().receive(datagramPacket);
                byte[] bytes = new byte[datagramPacket.getLength()];
                System.arraycopy(datagramPacket.getData(),0,bytes,0,datagramPacket.getLength());
                System.out.print("     "+bytes.length);
//                int length = datagramPacket.getLength();
//                byte[] bytes = new byte[length];
//                System.arraycopy(datagramPacket.getData(), 0, bytes, 0, length);
//                log.debug(new String(bytes));
//                getSock().send(new DatagramPacket("Has Receive".getBytes(),"Has Receive".length()));
//                log.debug("数据发送完成");
//                Thread.sleep(Integer.MAX_VALUE);

            } catch (Exception e) {
                log.error("error",e);
            }
        }
    }

    private  DatagramSocket getSock() {
        if (sock == null) {
            initPortMonitor(listenPort);
        }
        return sock;
    }
    private  void initPortMonitor(int listenPort) {
        try {
            sock = new DatagramSocket(null);
            sock.setReuseAddress(true);
            /*
            Socket选项TrafficClass
                IP规定了以下4种服务类型，用来定性地描述服务的质量：

                IPTOS_LOWCOST ( 0x02): 发送成本低。
                IPTOS_RELIABILITY ( 0x04):高可靠性，保证把数据可靠地送到目的地。
                IPTOS_THROUGHPUT ( 0x08 ):最高吞吐量，一次可以接收或者发送大批量的数据。
                IPTOS_ LOWDELAY ( 0x10):最小延迟，传输数据的速度快，把数据快速送达目的地。
                这4种服务类型还可以使用“或” 运算进行相应的组合。

                public void setTrafficClass(int tc)方法的作用是为从此Socket上发送的包在IP头中设置流量类别(traffic class)。
                public int getTrafficClass()方法的作用是为从此Socket上发送的包获取IP头中的流量类别或服务类型。
                当向IP头中设置了流量类型后，路由器或交换机就会根据这个流量类型来进行不同的处理，同时必须要硬件设备进行参与处理。
             */
            sock.setTrafficClass(0x08);
            sock.setReceiveBufferSize(8*1024);
            sock.bind(new InetSocketAddress(listenPort));

            log.debug("初始化监听完成，REUSEADDR:{} 接收区大小是:{}",sock.getReuseAddress(),sock.getReceiveBufferSize());
            log.debug("初始化监听完成，接收区大小是:{}",sock.getReceiveBufferSize());
        } catch (Exception e) {
            log.error("error",e);
        }
    }
}