package com.sailing.demo.vscg.utils;


import com.sailing.demo.vscg.pojo.Serverpojo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Createserver
{

    Serverpojo serverpojo = new Serverpojo();
    DatagramSocket server = new DatagramSocket(serverpojo.getServerport());

    public Createserver() throws SocketException {
        //创建接收端的Socket服务对象，并且指定端口号
        //端口
        int port = 10010;
        serverpojo.setServerport(port);
        System.out.println("服务端已开启");
    }



    public void serverjieshou() throws IOException {
        /**
         * 服务端接收客户端INVITE（第一次接收信息）
         */
        //创建一个数据包，用于接收数据
        byte[] bys = new byte[1024];
        DatagramPacket dp = new DatagramPacket(bys, bys.length);

        //接收数据
        server.receive(dp);


        //获取数据
        String data = new String(dp.getData(),0,dp.getLength());
        System.out.println(data);
    }





}