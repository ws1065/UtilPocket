package com.sailing;

import com.sailing.hessian.HessianTest;
import com.sailing.sipControl.SIPControl;
import com.sailing.tcp.SimpleTcpClient;
import com.sailing.tcp.SimpleTcpServer;
import com.sailing.udp.SimpleClient;
import com.sailing.udp.SimpleServer;
import com.sailing.videoOneWay.OnewayTest;

import java.util.Arrays;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-08-21 09:58
 */

public class Router {
    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            printUsage();
            System.exit(0);
        }else {
            String router = args[0];
            switch (router){
                case "oneway":
                    OnewayTest.run(args);
                    break;
                case "NetWorkIntercept":
//                    NetWorkIntercept.run();
                    break;
                case "NetWorkUtil":
//                    NetWorkUtil.run();
                    break;
                case "hessianTest":
                    HessianTest.run();
                    break;
                case "tcpClient":
                    new SimpleTcpClient().run(args[1],args[2]);
                    break;
                case "tcpServer":
                    new SimpleTcpServer().start(args[1],args[2]);
                    break;
                case "udpClient":
                    new SimpleClient().run();
                    break;
                case "udpServer":
                    new SimpleServer(Integer.valueOf(args[1])).start();
                    break;
                case "ssh" :
                    SshExecuter.run(args);
                    break;
                case "RTPTransmit":
                    AVTransmit2.main(new String[]{args[1],args[2],args[3]});
                    break;
                    /*
                     SIPControl-allow    1     34020000005213200001 172.20.52.150 5060
                     */
                case "SIPControl-allow":
                    for (int i = 0; i < Integer.parseInt(args[1]); i++) {
                        SIPControl.NotAllow("34020000005213200001",args[2],args[3],"172.20.54.131","34020000005213200002");
                        Thread.sleep(1000);
                    }
                    break;


                case "sgg":
                    // java -jar sgg(固定字符串)  requestSggIp(网闸的地址) sggMonitorIp(通道的监听IP) sggLinkIp(通道的连接IP) sggTargetIp(通道的目标IP) status[start|stop](启停状态) protocol[tcp|udp](通道的协议) timeout[秒为单位](通道的超时时间) startPort(通道的监听端口起始) endPort(通道的监听端口停止)
                    String requestSgg =args[1];
                    int requestPort= 4399;
                    String sggMonitorIp =args[2];
                    String sggLinkIp =args[3];
                    String sggTargetIp =args[4];
                    String status = args[5];
                    String protocol =args[6];
                    String timeout = args[7];
                    int startPort = Integer.valueOf(args[8]);
                    int endPort = 0;
                    if (args.length == 10)
                        endPort = Integer.valueOf(args[9]);
                    else
                        endPort = startPort;
                    if (endPort !=0 && startPort <= endPort)
                        NoticeSgg.requestData(requestSgg,requestPort,startPort,endPort,
                                sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol,timeout);
                    else
                        throw new Exception("输入参数有误："+Arrays.toString(args));
                    break;
                case "noticesggself":
                    requestSgg =args[1];
                    requestPort= 4399;
                    int port = Integer.valueOf(args[2]);
                    String selfMonitorIp = args[3];
                    String selfLinkIp =args[4];
                    String selfTargetIp =args[5];
                    sggMonitorIp =args[6];
                    sggLinkIp =args[7];
                    sggTargetIp =args[8];
                    status = args[9];
                    protocol =args[10];
                    NoticeSgg.requestData(requestSgg,requestPort,port,selfMonitorIp,selfLinkIp,selfTargetIp,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol);
                    break;
            }
        }
    }

    private static void printUsage() {
        System.err.println("参数异常,请输入参数                                  ");
        System.err.println("java -jar test.jar  RTPTransmit    file:/C:/media/test.mov  129.130.131.132 42050      ");
        System.err.println("                                   音视频文件呢路径            单播地址        单播端口 ");
        System.err.println("java -jar test.jar  SIPControl-allow    times     dstDeviceId dstHost dstPort localHost localDeviceId ");
        System.err.println("                    times:循环发送次数                                                                 ");
        System.err.println("java -jar test.jar sgg   requestSgg      sggMonitorIp  sggLinkIp      sggTargetIp       status  protocol   startPort endPort     ");
        System.err.println("   requestSgg  请求的网闸的ip需要当前机器能连的上这个IP\n" +
//                           "   selfMonitorIp 当前的机器监听的IP\n" +
//                           "   selfLinkIp 当前的机器连接的IP\n" +
//                           "   selfTargetIp 当前机器发送包的目标IP\n" +
                           "   sggMonitorIp 网闸配置中的监听IP\n" +
                           "   sggLinkIp  网闸配置中连接IP    \n" +
                           "   sggTargetIp   网闸配置中的目标IP \n" +
                           "   status 需要执行的状态\"start\" 或者\"stop\"\n" +
                           "   protocol  传输的协议  tcp udp  " +
                           "   startPort 起始监听端口   \n"  +
                           "   endPort    终止监听端口，可不填   \n"  +
                "");
    }
}