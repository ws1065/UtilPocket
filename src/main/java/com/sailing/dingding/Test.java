package com.sailing.dingding;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

public class Test{
    public static void main(String[] args) throws Exception{

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        //获取网卡流量
        long[] beferNetworkRecvAndSents = networkInterfaceRecvAndSent(hal.getNetworkIFs());

        long[] afterNetworkRecvAndSents = networkInterfaceRecvAndSent(hal.getNetworkIFs());
        System.out.println((afterNetworkRecvAndSents[0]-beferNetworkRecvAndSents[0]));
        System.out.println((afterNetworkRecvAndSents[1]-beferNetworkRecvAndSents[1]));

    }

    private static long[] networkInterfaceRecvAndSent(NetworkIF[] networkIFs) {
        long recvTotal = 0;
        long sentTotal = 0;
        for (NetworkIF net : networkIFs) {
            recvTotal +=  net.getBytesRecv();
            sentTotal += net.getBytesSent();
        }
        return new long[]{recvTotal,sentTotal};
    }

}