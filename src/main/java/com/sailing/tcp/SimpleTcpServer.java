package com.sailing.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 16:37
 */
public class SimpleTcpServer extends Thread {

    private ServerSocket sock;
    private int connect;

    public static void main(String[] args) throws IOException {

        byte[] bytes = {0, 0, 0, 0};
        String s = new String(bytes);
        byte[] bytes1 = "  ".getBytes();
        byte[] bytes2 = "\r\n".getBytes();
        System.out.println(Arrays.toString(bytes1));
        System.out.println(Arrays.toString(bytes2));
        System.out.println();



        new SimpleTcpServer().start("15060","3000");
    }

    public void start(String portStr, String connectTimeoutStr)  {
        int port = Integer.parseInt(portStr);
        this.connect = Integer.parseInt(connectTimeoutStr);
        try {
            Thread myThread = new Thread(this);
            myThread.setName("TCPMessageProcessorThread");
            myThread.setDaemon(true);
            this.sock = new ServerSocket(port, 0, InetAddress.getByName("0.0.0.0"));
            myThread.start();
            new Thread(()->{
                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("启动tcp接收数据的时候捕获一个未知异常,msg:{}"+e.getMessage());
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket socket = this.sock.accept();
                new ServerAcceptNew(socket,connect);
            }catch (Exception e){
                System.out.println("启动tcp接收数据的时候捕获一个未知异常,msg:"+e.getMessage());
            }
        }
    }
}