//package com.sailing.handleVscgApp;
//
//import com.caucho.hessian.client.HessianProxyFactory;
//import com.sailing.dscg.entity.RespData;
//import com.sailing.dscg.entity.passageway.Passageway;
//import com.sailing.dscg.entity.passageway.PassagewayParam;
//import com.sailing.dscg.interfaces.ExecCommend;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 测试方法
// *  测试新的流媒体转发通道
// *  开通以及关闭端口
// */
//@Component
//@Slf4j
//public class StartProxyDemo {
//
//    private static final int forTime = 1;
//    // 服务器端CB接口
//    private static final String cbServerInterfaceUrl = "http://172.20.52.61:8000/ExecCommend";
//    private static  ExecCommendImplForEngine2CImpl execCommendImplForEngine2C = new ExecCommendImplForEngine2CImpl();
//
//    public static void run(String[] args) {
//        //Client Commonds: startproxy isClient reqHost isStart isTcp monitorIp remoteIp postIp startPort portLength
//        //Server Commonds: startproxy isClient reqHost isStart isTcp monitorIp startPort portLength
//        String isClient = args[0];
//        if ("true".equalsIgnoreCase(isClient)){
//            String reqHost= args[1];;
//            String isStart = args[2];;
//            String isTcp = args[3];
//            String monitorIp = args[4];
//            String remoteIp = args[5];
//            String postIp = args[6];
//            String startPort = args[7];
//            String portLength = args[8];
//            startClientPassageway("http://"+reqHost+":8000/ExecCommend",monitorIp,remoteIp,postIp,
//                    Integer.parseInt(startPort),Integer.parseInt(portLength),
//                    Boolean.parseBoolean(isTcp),Boolean.parseBoolean(isStart));
//
//        }else if ("false".equalsIgnoreCase(isClient)){
//
//            String reqHost= args[1];;
//            String isStart = args[2];;
//            String isTcp = args[3];
//            String monitorIp = args[4];
//            String startPort = args[5];
//            String portLength = args[6];
//            startServerPassageway("http://"+reqHost+":8000/ExecCommend",monitorIp,
//                    Integer.parseInt(startPort),Integer.parseInt(portLength),
//                    Boolean.parseBoolean(isTcp),Boolean.parseBoolean(isStart));
//        }
//    }
//
//
//    public static void main(String[] args) {
//
////        startClientPassageway(cbServerInterfaceUrl,"172.20.54.61",
////                "192.168.10.61","172.20.52.130",12200,200,true,true);
//        startServerPassageway(cbServerInterfaceUrl,"192.168.10.61",
//                12200,200,true,true);
//
//
//    }
//
//    private static void startClientPassageway(String reqUrl,String monitorIp,String remoteIp,String postIp,
//                                              int startPort,int portLength,boolean isTcp,boolean isStart){
//        boolean isClient = true;
//        for (int i = startPort; i < portLength + startPort; i++) {
//            long l1 = System.currentTimeMillis();
//            startPassagewayAction(reqUrl,i, i, isTcp, isStart, isClient, monitorIp, remoteIp, postIp);
//            long l2 = System.currentTimeMillis();
//            System.out.println(i+"端口耗时"+(l2-l1)/1000D);
//        }
//    }
//    private static void startServerPassageway(String reqUrl,String monitorIp,
//                                              int startPort,int portLength,boolean isTcp,boolean isStart){
//        boolean isClient = false;
//        for (int i = startPort; i < portLength + startPort; i++) {
//            long l1 = System.currentTimeMillis();
//            startPassagewayAction(reqUrl,i, i, isTcp, isStart, isClient, monitorIp, null, null);
//            long l2 = System.currentTimeMillis();
//            System.out.println(i+"端口耗时"+(l2-l1)/1000D);
//        }
//    }
//
//
//    private static void startPassagewayAction(String reqUrl, int index, int port,
//                                              boolean isTcp, boolean isStart, boolean isClient, String monitorIp, String remoteIp, String postIp) {
//        RespData respData = null;
//        try {
//            HessianProxyFactory factory = new HessianProxyFactory();
//            String id = "RTP-AUTO-CB-create-" + index;
//            Passageway passageway = new Passageway();
//            passageway.setId(id);
//            passageway.setDirection(null);
//            passageway.setName(id);
//            passageway.setType(isTcp ? "TCP": "UDP");
//            passageway.setPortType(true);
//            passageway.setTcpBuffMaxCount(128);
//            passageway.setSggMultiLines(500);
//            passageway.setState(isStart ? "start" : "stop");
//            passageway.setClientOrServer(isClient ? 0 : 1);
//
//
//            PassagewayParam param = new PassagewayParam();
//            param.setId(port);
//            param.setPassagewayId(passageway.getId());
//            param.setMonitorIp(monitorIp);
//            param.setMonitorPort(String.valueOf(port));
//            param.setTargetIp(remoteIp);
//            param.setTargetPort(remoteIp==null?"0":param.getMonitorPort());
//            param.setPostIp(isClient ? postIp : null);
//            param.setPostPort(isClient ? port : 0);
//            param.setPostProtocol("TCP".equalsIgnoreCase(passageway.getType())?1:0);
//            List<PassagewayParam> list = new ArrayList<>();
//            list.add(param);
//            passageway.setPassagewayParams(list);
//
//            ExecCommend execCommend = (ExecCommend) factory.create(ExecCommend.class, reqUrl);
//            long start = System.currentTimeMillis();
//            if (isStart) {
//                respData = execCommendImplForEngine2C.startPassageway(passageway);
//            } else {
//                respData = execCommendImplForEngine2C.stopPassageway(passageway);
//            }
//            long end = System.currentTimeMillis();
//            long gap = end - start;
//            log.info(String.format("操作通道[%s]耗时:%s毫秒", index, gap));
//        } catch(MalformedURLException e) {
//            log.error(e.getMessage(), e);
//        } catch (Exception e) {
//            log.error("CB内部通信异常:" + e.getMessage());
//            throw new RuntimeException("CB内部通信异常");
//        }
//        log.info("CB测试启停通道，结果:" + respData);
//    }
//
//
//}
//
