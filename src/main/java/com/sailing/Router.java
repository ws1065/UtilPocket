package com.sailing;

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
        }else if (args.length == 1){

        }else {
            String router = args[0];
            switch (router){
                case "simpleServer":
                    new SimpleServer(new SysProperties(args[1]));
                    break;
                case "ssh" :
                    SshExecuter.run(args);
                    break;
                case "RTPTransmit":
                    if (args.length != 4) {
                        printUsage();
                        System.exit(0);
                    }
                    AVTransmit2.main(new String[]{args[1],args[2],args[3]});
                    break;
                case "SIPControl-allow":
                    if (args.length != 7) {
                        printUsage();
                        System.exit(0);
                    }
                    for (int i = 0; i < Integer.parseInt(args[1]); i++) {
                        SIPControl.NotAllow(args[2],args[3],args[4],args[5],args[6]);
                    }
                    break;


                case "sgg":
                    if (!(args.length == 8 || args.length == 9 )) {
                        printUsage();
                        System.exit(0);
                    }
                    String requestSgg =args[1];
                    int requestPort= 4399;
                    String sggMonitorIp =args[2];
                    String sggLinkIp =args[3];
                    String sggTargetIp =args[4];
                    String status = args[5];
                    String protocol =args[6];
                    int startPort = Integer.valueOf(args[7]);
                    int endPort = 0;
                    if (args.length == 9)
                        endPort = Integer.valueOf(args[8]);
                    else
                        endPort = startPort;
                    if (endPort !=0 && startPort <= endPort)
                        NoticeSgg.requestData(requestSgg,requestPort,startPort,endPort,
                                sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol);
                    else
                        throw new Exception("输入参数有误："+Arrays.toString(args));
                    break;
                case "noticesggself":
                    if (args.length != 11) {
                        printUsage();
                        System.exit(0);
                    }
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