package com.sailing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-06-08 21:08
 */
public class MonitorArraysSize implements Runnable {
    private static Logger log = LoggerFactory.getLogger(MonitorArraysSize.class);

    public static void main(String[] args) {
        new Thread(new MonitorArraysSize()).start();
    }
    @Override
    public void run() {
        int debugPort = 5454;
        if ( debugPort!=0 ) {
            while (true) {
                try (ServerSocket serverSocket = new ServerSocket(debugPort)){
                    Socket socket = serverSocket.accept();
                    byte[] buf = new byte[20 * 1024];
//                    try {
//                        socket.receive(packet);
//                    } catch (Exception e) {
//                        log.error("监控线程：通道测试的测试数据出错，msg:{}",e.getMessage());
//                    }
                    try {
                        String s = rePort();
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(s.getBytes("gb2312"));
                    } catch (Exception e) {
                        log.error("监控线程报错，msg:{}",e.getMessage());
                    }
                } catch (Exception e) {
                    log.error("初始化监控线程端口报错，msg:{}",e.getMessage());
                }
            }
        }
    }

    private String rePort() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("接收队列长度：");
        sb.append(System.lineSeparator());

        sb.append("接收队列长度：");
        sb.append(System.lineSeparator());
        sb.append("请求处理队列长度：");
        sb.append(System.lineSeparator());
        sb.append("回复处理长度：");
        sb.append(System.lineSeparator());
        sb.append("发送队列长度：");
        sb.append(System.lineSeparator());

        String s = sb.toString();
        log.debug(s);
        return s;
    }
}
