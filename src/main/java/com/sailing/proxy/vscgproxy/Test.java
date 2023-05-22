//package com.sailing.proxy.vscgproxy;
//
//import com.sailing.dscg.entity.RespData;
//import com.sailing.dscg.entity.passageway.Passageway;
//import com.sailing.dscg.entity.passageway.PassagewayParam;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * @program: demo
// * @description:
// * @author: wangsw
// * @create: 2021-09-14 00:13
// */
//public class Test {
//    public static void main(String[] args) {
//
//        Passageway passageway1 = getServerPassageway();
//        ExecCommendImplForEngine2CImpl exec = new ExecCommendImplForEngine2CImpl();
//
//        RespData<Boolean> booleanRespData = exec.startPassageway(passageway1);
//        System.out.println();
//    }
//
//    private static Passageway getServerPassageway() throws RuntimeException {
//        String protocol = "TCP";
//        int clientOrServer = 1;
//        String monitorIp = "192.168.91.132";
//        Integer listenPort = 15555;
//        String status = "start";
//
//        Passageway passageway = new Passageway();
//        String name = "RTP-AUTO-" + listenPort;
//        String passageID = UUID.randomUUID().toString() + "-" + listenPort;
//        passageway.setName(name);
//        passageway.setType(protocol.toUpperCase());
//        passageway.setDirection(null);
//        passageway.setPortType(true);
//        passageway.setTcpBuffMaxCount(128);
//        passageway.setSggMultiLines(500);
//        passageway.setState(status);
//        passageway.setClientOrServer(0);
//
//        passageway.setTcpBuffMaxCount(128);
//        passageway.setSggMultiLines(5);
//        passageway.setPortType(true);
//
//        passageway.setTimeOut(0);
//        passageway.setPortType(true);
//        passageway.setId(passageID);
//        //流插白帧相关
//        passageway.setShouldStat(0);
//        passageway.setShouldFrame(0);
//        passageway.setIntervalOfFrame(1);
//        passageway.setLengthOfFrame(1);
//
//        List<PassagewayParam> passagewayParams = new ArrayList<>();
//        PassagewayParam passagewayParam = new PassagewayParam();
//        passagewayParam.setPassagewayId(passageID);
//        passagewayParam.setMonitorIp(monitorIp);
//        passagewayParam.setMonitorPort(String.valueOf(listenPort));
//        passagewayParams.add(passagewayParam);
//        passageway.setPassagewayParams(passagewayParams);
//        /*
//        该字段仅针对VSCG项目 VSCG的引擎，在调用VSCG的CB的时候，
//        进行传参 在创建VSCG的通道的时候，数据流的发起端，
//        是client端,值为0 数据流的接收端，是server端，值为1
//         */
//        passageway.setClientOrServer(clientOrServer);
//
//        return passageway;
//    }
//    private static Passageway getClientPassageway() throws RuntimeException {
//        String protocol = "TCP";
//        String monitorIp = "192.168.2.133";
//        String targetIp = "192.168.91.132";
//        String postIp = "192.168.2.131";
//        String status = "stop";
//        Integer listenPort = 16666;
//        int targetPort = 14444;
//
//        Passageway passageway = new Passageway();
//        String name = "RTP-AUTO-" + listenPort;
//        String passageID = UUID.randomUUID().toString() + "-" + listenPort;
//        passageway.setName(name);
//        passageway.setType(protocol.toUpperCase());
//        passageway.setDirection(null);
//        passageway.setPortType(true);
//        passageway.setTcpBuffMaxCount(128);
//        passageway.setSggMultiLines(500);
//        passageway.setState(status);
//        passageway.setClientOrServer(0);
//
//        passageway.setTcpBuffMaxCount(128);
//        passageway.setSggMultiLines(5);
//        passageway.setPortType(true);
//
//        passageway.setTimeOut(0);
//        passageway.setPortType(true);
//        passageway.setId(passageID);
//        //流插白帧相关
//        passageway.setShouldStat(0);
//        passageway.setShouldFrame(0);
//        passageway.setIntervalOfFrame(1);
//        passageway.setLengthOfFrame(1);
//
//        List<PassagewayParam> passagewayParams = new ArrayList<>();
//        PassagewayParam passagewayParam = new PassagewayParam();
//        passagewayParam.setPassagewayId(passageID);
//
//
//        passagewayParam.setMonitorIp(monitorIp);
//        passagewayParam.setMonitorPort(String.valueOf(listenPort));
//        passagewayParam.setTargetIp(targetIp);
//        passagewayParam.setTargetPort(String.valueOf(targetPort));
//        passagewayParam.setPostIp(postIp);
//        passagewayParam.setPostPort(listenPort);
//        passagewayParams.add(passagewayParam);
//        passageway.setPassagewayParams(passagewayParams);
//        /*
//        该字段仅针对VSCG项目 VSCG的引擎，在调用VSCG的CB的时候，
//        进行传参 在创建VSCG的通道的时候，数据流的发起端，
//        是client端,值为0 数据流的接收端，是server端，值为1
//         */
//        passageway.setClientOrServer(0);
//
//        return passageway;
//    }
//}