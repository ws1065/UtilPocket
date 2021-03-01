//package com.sailing.arp;
//
//import jpcap.JpcapCaptor;
//import jpcap.NetworkInterface;
//import jpcap.NetworkInterfaceAddress;
//
//import java.util.Arrays;
//import java.util.Scanner;
//
///**
// * Created by duke on 2016/11/16.
// * arp-client
// */
//public class NetWorkUtil
//{
//    public static void run() {
//        System.out.println(">>>>>>>>>>>>>>>>>>>");
//        getDevice();
//    }
//
//    /**
//     * 使用String的startsWith函数判断IP相同的开始部分相同即可
//     * @param segment 例如：192.168.1
//     * @return
//     */
//    public static NetworkInterface getDevice(String segment)
//    {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        for (int i =0;i<devices.length;i++)
//        {
//            NetworkInterfaceAddress[] addresses = devices[i].addresses;
//            if(addresses[1].address.toString().startsWith(segment)){
//                return  devices[i];
//            }
//        }
//        return devices[0];
//    }
//    public static NetworkInterface getDevice()
//    {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        for (int i = 0; i < devices.length; i++) {
//            int j = i+1;
//            System.out.print(j +". ");
//            if (devices[i].addresses.length>0) {
//                System.out.println(devices[i].name + " " + devices[i].addresses[0].address);
//            }else {
//                System.out.println(devices[i].name + " " + Arrays.toString(devices[i].addresses));
//            }
//        }
//        System.out.println("请选择网卡");
//        Scanner scan = new Scanner(System.in);
//        int i = scan.nextInt();
//        NetworkInterfaceAddress[] addresses = devices[i-1].addresses;
//        return devices[i-1];
//    }
//}
