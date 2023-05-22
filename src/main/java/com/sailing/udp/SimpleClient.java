package com.sailing.udp;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-12-15 10:57
 */
public class SimpleClient {
    private static InetSocketAddress address;
    private static DatagramSocket datagramSocket;

    public static ArrayBlockingQueue<String> queue= new ArrayBlockingQueue(10000);

    public static AtomicInteger port = new AtomicInteger(1);
    private static CountDownLatch count= new CountDownLatch(30000);


    //
    public static HashMap<String,String> ansysExcel(String excelFilePath) {
        // 读取excel文件并且获得其中的deviceID列和IP列和名字列
        // 读取excel文件并且获得其中的deviceID列和IP列和名字列
        ExcelReader reader = ExcelUtil.getReader("C:\\Users\\wangw\\Desktop\\demo.xlsx");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < reader.getRowCount(); i++) {
            String deviceID = reader.getCell(i, 0).toString();
            String ip = reader.getCell(i, 1).toString();
            String name = reader.getCell(i, 2).toString();
            map.put(deviceID, ip + "," + name);
        }
    return map;

    }




    public static void main(String[] args) throws SocketException, InterruptedException {
        ansysExcel("");



//        String s = "20000000444849500000000000000000490000000000000049000000000000007b20226d6574686f6422203a20224448446973636f7665722e736561726368222c2022706172616d7322203a207b20226d616322203a2022222c2022756e6922203a2031207d207d0a";
//
//        byte[] bytes = hexStringToString(s);
//        run("172.20.50.66",37810,bytes);
        String s = "3c3f786d6c2076657273696f6e3d22312e302220656e636f64696e673d225554462d38223f3e0a3c534f41502d454e563a456e76656c6f706520786d6c6e733a534f41502d454e563d22687474703a2f2f7777772e77332e6f72672f323030332f30352f736f61702d656e76656c6f70652220786d6c6e733a534f41502d454e433d22687474703a2f2f7777772e77332e6f72672f323030332f30352f736f61702d656e636f64696e672220786d6c6e733a7873693d22687474703a2f2f7777772e77332e6f72672f323030312f584d4c536368656d612d696e7374616e63652220786d6c6e733a7873643d22687474703a2f2f7777772e77332e6f72672f323030312f584d4c536368656d612220786d6c6e733a786f703d22687474703a2f2f7777772e77332e6f72672f323030342f30382f786f702f696e636c7564652220786d6c6e733a7773613d22687474703a2f2f736368656d61732e786d6c736f61702e6f72672f77732f323030342f30382f61646472657373696e672220786d6c6e733a746e733d22687474703a2f2f736368656d61732e786d6c736f61702e6f72672f77732f323030352f30342f646973636f766572792220786d6c6e733a646e3d22687474703a2f2f7777772e6f6e7669662e6f72672f76657231302f6e6574776f726b2f7773646c2220786d6c6e733a777361353d22687474703a2f2f7777772e77332e6f72672f323030352f30382f61646472657373696e67223e3c534f41502d454e563a4865616465723e3c7773613a4d65737361676549443e75726e3a757569643a31313936352d312d643638612d316464322d313162322d613130352d3031303230333034303530363c2f7773613a4d65737361676549443e3c7773613a546f20534f41502d454e563a6d757374556e6465727374616e643d2274727565223e75726e3a736368656d61732d786d6c736f61702d6f72673a77733a323030353a30343a646973636f766572793c2f7773613a546f3e3c7773613a416374696f6e20534f41502d454e563a6d757374556e6465727374616e643d2274727565223e687474703a2f2f736368656d61732e786d6c736f61702e6f72672f77732f323030352f30342f646973636f766572792f50726f62653c2f7773613a416374696f6e3e3c2f534f41502d454e563a4865616465723e3c534f41502d454e563a426f64793e3c746e733a50726f62653e3c746e733a54797065733e646e3a4e6574776f726b566964656f5472616e736d69747465723c2f746e733a54797065733e3c2f746e733a50726f62653e3c2f534f41502d454e563a426f64793e3c2f534f41502d454e563a456e76656c6f70653e";

        byte[] bytes = hexStringToString(s);
        run("172.20.50.253",3702,bytes);

    }
    public static byte[] hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baKeyword;
    }

    private static byte[] intToBytes(long n, int offset, boolean big){
        byte[] array = new byte[4];
        if(big){
            array[3+offset] = (byte) (n & 0xff);
            array[2+offset] = (byte) (n >> 8 & 0xff);
            array[1+offset] = (byte) (n >> 16 & 0xff);
            array[offset] = (byte) (n >> 24 & 0xff);
        }else{
            array[offset] = (byte) (n & 0xff);
            array[1+offset] = (byte) (n >> 8 & 0xff);
            array[2+offset] = (byte) (n >> 16 & 0xff);
            array[3+offset] = (byte) (n >> 24 & 0xff);
        }
        return array;
    }

    public static void run(String host, int port, byte[] bytes) {
        try {
            address = new InetSocketAddress(host, port);
            datagramSocket = new DatagramSocket();

            DatagramPacket datagramPacket = null;

            datagramPacket = new DatagramPacket(bytes, bytes.length, address);
            //发送

                datagramSocket.send(datagramPacket);

            datagramSocket.receive(datagramPacket);
            System.out.print("     "+new String(datagramPacket.getData()));

            Thread.sleep(1000);
//            byte[] buf = new byte[20 * 1024];
//            DatagramPacket datagramPacket1 = new DatagramPacket(buf, buf.length);
////            //使用同一个socket接收
//            datagramSocket.receive(datagramPacket1);
//            System.out.println(new String(datagramPacket1.getData()));
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}