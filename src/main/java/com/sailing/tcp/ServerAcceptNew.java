package com.sailing.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 15:39
 */
@Slf4j
public class ServerAcceptNew extends Thread {

    private  int connect;
    private InputStream myClientInputStream= null;
    private  Socket mySock = null;
    private InetAddress peerAddress = null;
    private  InetAddress myAddress= null;
    private String peerProtocol;
    private  int peerPort = 0 ;
    private Thread myThread;
    private OutputStream myClientOutputStream= null;

    public ServerAcceptNew(Socket socket, int connect){
        try {
            mySock = socket;
            mySock.setKeepAlive(true);
            //mySock.setSoTimeout(Integer.MAX_VALUE);
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
        StringBuffer sb = new StringBuffer();
        boolean b = true;
        while (b) {
            try {
                BufferedInputStream reader = new BufferedInputStream(myClientInputStream);

                byte[] arr =new byte[1024];

                int len;//返回读取到的个数
                //使用循环开始读取
                while((len=reader.read(arr))!=-1){
                    byte[] temp = new byte[len];
                    System.arraycopy(arr,0,temp,0,len);
                    System.out.println(temp.length);
                }
                System.out.println(len);
            }catch (Exception e){
                try {
                    long l1 = System.currentTimeMillis();
                    log.error("tcp接收数据时候,捕获一个Connection reset,关闭socket连接"+mySock,e);
                    myClientOutputStream.flush();
                    myClientOutputStream.close();
                    myClientInputStream.close();
                    mySock.close();
                } catch (Exception ex) {
                    log.error("关闭socket出错{}",ex.getMessage());
                }
                break;
            }
        }
    }

    byte[] concat(byte[] a, byte[] b) {

        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;

    }
}