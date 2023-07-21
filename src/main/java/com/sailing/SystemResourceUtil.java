package com.sailing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description:
 * @Auther:史俊华
 * @Date:2020/5/909
 */
@Slf4j
public class SystemResourceUtil {
    public static void getSystemResource(){
        try {
            long n1 = CurrentTimeMillisClock.getInstance().now();
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            long n2 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n2-n1)/1000d);
            OperatingSystem os = si.getOperatingSystem();
            long n3 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n3-n2)/1000d);
//            获取操作系统
            String systemInfo = os.getFamily()+ os.getVersionInfo().getVersion();
            System.out.println("systemInfo:"+systemInfo);
            long n4 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n4-n3)/1000d);
//            获取系统运行时长
            String upTime = FormatUtil.formatElapsedSecs(os.getSystemUptime());
            System.out.println("upTime:"+upTime.replace("days","天"));
            long n5 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n5-n4)/1000d);
//            获取CPU核数
            int cpuCores = hal.getProcessor().getPhysicalProcessorCount();
            System.out.println("获取CPU核数:"+cpuCores);
            long n6 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n6-n5)/1000d);
            //获取网卡流量
            long[] beferNetworkRecvAndSents = networkInterfaceRecvAndSent(hal.getNetworkIFs());
            long n7 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("获取网卡流量 time:"+(n7-n6)/1000d);
            //CPU百分比
            CentralProcessor processor = hal.getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            Util.sleep(1000L);
            double cpuAverage = processor.getSystemCpuLoadBetweenTicks(prevTicks);
            BigDecimal cpuBg = new BigDecimal(cpuAverage*100);
            double cpuUsePercent = cpuBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            System.out.println("CPU百分比:"+cpuUsePercent);
            long n8 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n8-n7)/1000d);
            //内存
            GlobalMemory memory = hal.getMemory();
            long memoryAvailable = memory.getAvailable();
            long memoryTotal = memory.getTotal();
            long memoryUse = memoryTotal-memoryAvailable;
            BigDecimal bg = new BigDecimal(memoryUse*100/memoryTotal);
            double memoryUsePercent = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            System.out.println("内存:"+memoryUsePercent);
            long n9 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n9-n8)/1000d);
            //硬盘
            long diskAvailableTotal = 0;
            long diskTotal = 0;

            FileSystem fileSystem = os.getFileSystem();
            OSFileStore[] fsArray = fileSystem.getFileStores();
            for (OSFileStore fs : fsArray) {
                long usable = fs.getUsableSpace();
                long total = fs.getTotalSpace();
                diskAvailableTotal += usable;
                diskTotal += total;
            }
            long diskUse = diskTotal - diskAvailableTotal;
            BigDecimal diskBg = new BigDecimal(diskUse*100/diskTotal);
            double diskUsePercent = diskBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            System.out.println("硬盘:"+diskUsePercent);
            long n10 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n10-n9)/1000d);
            //获取网卡流量
            long[] afterNetworkRecvAndSents = networkInterfaceRecvAndSent(hal.getNetworkIFs());
            long networkIn = afterNetworkRecvAndSents[0] - beferNetworkRecvAndSents[0];
            long networkOut = afterNetworkRecvAndSents[1] - beferNetworkRecvAndSents[1];
            long n11 = CurrentTimeMillisClock.getInstance().now();
            System.out.println("time:"+(n11-n10)/1000d);
            System.out.println("获取网卡流量:"+networkIn);
            System.out.println("获取网卡流量:"+networkOut);
            System.out.println("time:"+(n11-n1)/1000d);


            System.out.println(getIps());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //engine
    public static boolean getEngineStat() throws Exception {
        String cmds = "ps -ef|grep VSCG|grep -v grep";
        String rawDatas = SshExecuter.execSh(cmds);
        return StringUtils.isNotBlank(rawDatas);
    }

    //keepalived
    public static boolean shutdownKeepAlive() throws Exception {
        String cmds = "systemctl stop  keepalived ";
        String rawDatas = SshExecuter.execSh(cmds);
        return true;
    }
    public static boolean startKeepAlive() throws Exception {
        String cmds = "systemctl start  keepalived ";
        String rawDatas = SshExecuter.execSh(cmds);
        return true;
    }
    public static boolean getKeepAliveStat() throws Exception {
        String cmds = "systemctl status  keepalived |grep Active";
        String rawDatas = SshExecuter.execSh(cmds);
        boolean status = false;
        //解析执行的命令中的数据
        if (StringUtils.isNotBlank(rawDatas)) {
            for (String rawData : rawDatas.split(System.lineSeparator())) {
                if (rawData.trim().startsWith("Active")) {
                    if (rawData.contains("running")) {
                        status =  true;
                    }else if(rawData.contains("dead")) {
                        status =  false;
                    }else {
                        //当keepalive状态不是active或者dead的后
                        log.warn("[keepAlive state]{}",SshExecuter.execSh("systemctl status  keepalived"));
                        status =  false;
                    }
                }
            }
        }
        //拥有两个IP才说名状态ok
        //某个网卡是否具有两个IP
        boolean ip2 = false;
//        String cmd1 = "ip a |grep inet |grep -v grep|awk '{print $1}'";
//        String rawData1 = SshExecuter.execSh(cmd1);
//        if (StringUtils.isNotBlank(rawData1)) {
//            String[] inets = rawData1.split("inet6");
//            for (String inet : inets) {
//                if (StringUtils.countMatches(inet,"inet") == 2) {
//                    ip2 = true;
//                }
//            }
//        }
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        NetworkIF[] networkIFs = hal.getNetworkIFs();
        for (NetworkIF net : networkIFs) {
            if (net.getIPv4addr().length==2) {
                ip2 = true;
            }
        }
        return ip2 && status;
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

    public static List<String> getIps(){
        List<String> ips = new ArrayList<>();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        NetworkIF[] networkIFs = hal.getNetworkIFs();
        for (NetworkIF net : networkIFs) {
            List<String> c = Arrays.asList(net.getIPv4addr());
            System.out.println(c);
            ips.addAll(c);
            System.out.println(Arrays.asList(net.getIPv6addr()));
        }
        return ips;
    }
}
