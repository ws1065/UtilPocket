package com.sailing.tcp;

import org.apache.commons.lang.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 15:39
 */
public class ServerAccept extends Thread {

    private  int connect;
    private InputStream myClientInputStream= null;
    private  Socket mySock = null;
    private InetAddress peerAddress = null;
    private  InetAddress myAddress= null;
    private String peerProtocol;
    private  int peerPort = 0 ;
    private Thread myThread;
    private OutputStream myClientOutputStream= null;

    public ServerAccept(Socket socket, int connect){
        try {
            mySock = socket;
            this.connect = connect;
            if (connect>=0){
                mySock.setSoTimeout(connect);
            }
            //引入ioHandler为了操作socketTable中的socket关闭后线程仍然存在的问题
            //mySock.setSoTimeout(3*1000*60);
            peerAddress = this.mySock.getInetAddress();
            myAddress = socket.getLocalAddress();
            peerPort = socket.getPort();
            myClientInputStream = this.mySock.getInputStream();
            myClientOutputStream = this.mySock.getOutputStream();
            myThread = new Thread(this);
            myThread.setDaemon(true);
            myThread.setName("s2cThread-"+myAddress+"<-"+peerAddress+":"+peerPort+"-"+new Random().nextInt(1000));
            myThread.start();

        } catch (Exception e) {
            System.out.println("启动tcp接收数据的时候捕获一个未知异常,msg:"+e.getMessage());
        }
    }


    public void run() {
        short s_4096_2 = 4;
        while (true) {
            byte[] bytes = new byte[s_4096_2];
            try {
                BufferedInputStream reader = new BufferedInputStream(myClientInputStream);
                int read = reader.read(bytes, 0, bytes.length);
                while (reader.available() > 0) {
                    byte[] bytes2 = new byte[reader.available()];
                    read = reader.read(bytes2, 0, bytes2.length);
                    System.out.println(new String(bytes2));
                }
                if (read == -1) {
                    //这种定向处理仅仅使用与重庆地区的短连接的大数据包,对应处理下级的向上级的单次的请求
                    System.out.println("result==1");
                } else if(new String(bytes).trim().length() <3){
                    //解决接收到其他的tcp消息进行解析的时候报错
                    //log.warn("丢弃长度小于3的数据包:{}",new String(bytes).trim());
                } else {
                    System.out.println(new String(bytes));
                }
            }catch (Exception e){
                if (!e.getMessage().startsWith("Connection reset")) {
                    System.out.println("tcp接收数据时候,捕获一个未知异常,msg:"+ e.getMessage());
                    break;
                }else {
                    try {
                        //服务端或者客户端有问题了现在需要关闭程序
                        System.out.println("tcp接收数据时候,捕获一个Connection reset,关闭socket连接{}"+mySock);
                        if (Thread.currentThread().getName().startsWith("c2sThread")){
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}