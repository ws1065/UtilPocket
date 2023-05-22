package com.sailing.udp;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
@Slf4j
public class App 
{
    public static void main( String[] args )
    {
        DatagramSocket socket = null;
        while (true) {
            try {

                if (socket == null) {
                    socket = new DatagramSocket(48899);;
                }
                // 定义接收网络数据的字节数组
                byte[] inBuff = new byte[30 * 1024];
                // 以指定字节数组创建准备接收数据的DatagramPacket对象
                DatagramPacket request = new DatagramPacket(inBuff , inBuff.length);
                socket.receive(request);
                byte[] data = request.getData();
                ArrayList<String> list = JSONArray.parseObject(data, ArrayList.class);
                log.info("接收到数据为[{}]",list);
                new Thread(new ReceiveDemo(Integer.valueOf(list.get(2)))).start();
                new Thread(new SendDataDemo(list.get(4),Integer.valueOf(list.get(5)))).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

