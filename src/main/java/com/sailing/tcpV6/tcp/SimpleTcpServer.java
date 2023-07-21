package com.sailing.tcpV6.tcp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 16:37
 */
public class SimpleTcpServer extends Thread {

    private ServerSocket sock;
    private int connect;


    public static void main(String[] args) {
        new SimpleTcpServer().start("8899",10000+"");
    }

    public void start(String portStr, String connectTimeoutStr)  {
        int port = Integer.parseInt(portStr);
        this.connect = Integer.parseInt(connectTimeoutStr);
        try {
            Thread myThread = new Thread(this);
            myThread.setName("TCPMessageProcessorThread");
            myThread.setDaemon(true);
            this.sock = new ServerSocket(port, 0, InetAddress.getByName("::"));
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
                new ServerAccept(socket,connect);
            }catch (Exception e){
                System.out.println("启动tcp接收数据的时候捕获一个未知异常,msg:"+e.getMessage());
            }
        }
    }
}