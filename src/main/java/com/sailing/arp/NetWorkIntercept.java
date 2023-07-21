//package com.sailing.arp;
//
//import jpcap.*;
//import jpcap.packet.Packet;
//
//import java.nio.charset.Charset;
//
///**
// * Created by duke on 2016/11/15.
// * arp-client
// */
//public class NetWorkIntercept
//{
//    /*
//       拦截网卡数据包
//     */
//    public static void run() throws Exception {
//        //调用Arp欺诈函数，请移步ArpTest运行Main函数
//        System.out.println(System.getProperty("java.library.path"));
//        //获取同网段网卡
//        NetworkInterface device = NetWorkUtil.getDevice();
//        System.out.println("获取同网段网卡");
//        System.out.println(device);
//        //打开网卡
//        JpcapCaptor captor = JpcapCaptor.openDevice(device,65535,false,20);
//        System.out.println("打开网卡");
//        System.out.println(captor);
//        while (true)
//        {
//          //获取流量包，这个时间arp欺诈成功后，该主机的流量包就会通过你的主机网卡发送。
//          Packet p = captor.getPacket();
//          if(p!=null)
//          {
//              byte header_byte[] = p.data;
//              String header = new String(header_byte, Charset.defaultCharset());
//              //captor.setFilter("",true); 筛选函数还在研究怎么用舒服  这里约定协议会好用一点
////              if(header.contains("qichacha")) //我觉得这种更爽  哈哈哈
////              {
//                  System.out.println(header);//获取数据包干一些你该干的事情
//                  JpcapSender sender = captor.getJpcapSenderInstance();//获取
//                  sender.sendPacket(p);//数据包发送（
////              }
//          }
//
//      }
//
//    }
//}
