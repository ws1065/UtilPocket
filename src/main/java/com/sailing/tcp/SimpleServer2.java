package com.sailing.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2023-05-21 20:19
 */
@Slf4j
public class SimpleServer2 {


    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        ServerSocket sock = new ServerSocket(15060, 0, InetAddress.getByName("0.0.0.0"));
        new Thread(()->{
            startUDPServer(15060);
        }).start();
        while(true) {
            try {
                Socket socket = sock.accept();
                log.debug("receive num:{}",count.incrementAndGet());
                new Thread(()->{
                    receiveThread(socket);
                }).start();
            }catch (Exception e){
                System.out.println("启动tcp接收数据的时候捕获一个未知异常,msg:"+e.getMessage());
            }
        }
    }

    private static void startUDPServer(int port) {
        DatagramSocket udpSock;
        try {
            udpSock = new DatagramSocket(null);
            udpSock.setReuseAddress(true);
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

            udpSock.setTrafficClass(0x08);

            udpSock.setReceiveBufferSize(8*1024);
            udpSock.bind(new InetSocketAddress(port));

            log.debug("初始化监听完成，REUSEADDR:{} 接收区大小是:{}",udpSock.getReuseAddress(),udpSock.getReceiveBufferSize());
            log.debug("初始化监听完成，接收区大小是:{}",udpSock.getReceiveBufferSize());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            byte[] buf = new byte[20 * 1024];
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
                udpSock.receive(datagramPacket);
                log.debug("receive num:{}，{}",udpSock,count.incrementAndGet());
            } catch (Exception e) {
                log.error("error",e);
            }
        }
    }
    static byte[] concat(byte[] a, byte[] b) {

        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private static void receiveThread(Socket client) {
        new Thread(()->{
            while (true) {
                try {
                    BufferedInputStream reader = new BufferedInputStream(client.getInputStream());

                    int bytes;//返回读取到的个数
                    //使用循环开始读取
                    boolean rFlag = false;
                    boolean nFlag = false;
                    byte[] buffer = new byte[10240];
                    byte[] allBuffer = new byte[]{};
                    int len = 0;
                    int contentLength = -1;
                    while((bytes=reader.read())!=-1){

                        if (bytes == '\r'){
                            rFlag = true;
                        }
                        if (bytes == '\n'){
                            nFlag = true;
                        }
                        buffer[len] = (byte) bytes;
                        len++;

                        if (rFlag && nFlag){
                            rFlag = false;
                            nFlag = false;
                            String line = new String(buffer, 0, len,"gbk");
                            if (line.equals("\r\n") && contentLength > 0 && allBuffer.length>2 && allBuffer[allBuffer.length-1] == '\n'&& allBuffer[allBuffer.length-2] == '\r'){
                                allBuffer = concat(allBuffer, Arrays.copyOf(buffer,len));
                                byte[] body = new byte[contentLength];
                                int offset = 0;
                                int bodyLess = contentLength;
                                int bodyLen = 0;

                                while (bodyLess>0){
                                    try {
                                        bodyLen = reader.read(body,offset,bodyLess);
                                    }catch (Exception e){
                                        log.error("读取数据异常",e);
                                    }
                                    offset = bodyLen + offset;
                                    bodyLess = bodyLess - bodyLen;
                                }
                                allBuffer = concat(allBuffer,body);

                                log.debug("receive num:{}，{}",client,count.incrementAndGet());

                                allBuffer = new byte[]{};
                                len = 0;
                            }else if (line.equals("\r\n") && contentLength == 0 && allBuffer.length>2 && allBuffer[allBuffer.length-1] == '\n'&& allBuffer[allBuffer.length-2] == '\r'){
                                allBuffer = concat(allBuffer,Arrays.copyOf(buffer,len));
                                log.debug("receive num:{}，{}",client,count.incrementAndGet());


                                allBuffer = new byte[]{};
                                len = 0;
                            }else if (line.startsWith("Content-Length:")){
                                String[] split = line.split(":");
                                contentLength = Integer.parseInt(split[1].trim());
                                allBuffer = concat(allBuffer,Arrays.copyOf(buffer,len));
                                len = 0;
                            }else {
                                allBuffer = concat(allBuffer,Arrays.copyOf(buffer,len));
                                len = 0;
                            }
                        }

                        //修复BUG 有心跳socket连接但是不发数据，但是这个心跳socket会顶掉socketMap中的正常使用的socket
                        // 所以只有有数据读取过程中才会保存这个socket
                    }
                    if (bytes == -1){
                        //服务端或者客户端有问题了现在需要关闭程序
                        log.warn("客户端关闭,关闭socket连接{}",client);
                        //修复BUG 因为当前存储的KEY是地址，同地址有多个客户端连接时候 某一个客户端连接断开下
                        //会将存储的key删掉，造成response没有可用的socket返回的bug
                        //tcpHandler.removeSocket(key);
                        break;
                    }

                }catch (Exception e){
                    try {
                        //服务端或者客户端有问题了现在需要关闭程序
                    } catch (Exception ex) {
                        log.error("关闭socket出错{}",ex.getMessage());
                    }
                    break;
                }
            }


        }).start();
    }


}