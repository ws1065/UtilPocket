package com.sailing;


import com.caucho.hessian.client.HessianProxyFactory;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.passageway.Passageway;
import com.sailing.dscg.entity.passageway.PassagewayParam;
import com.sailing.dscg.interfaces.ExternalCommend;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求网闸开放端口，并且返回相应的状态
 * @author :wsw
 */
public class NoticeSgg {




    //向cb请求数据
    public static  void requestData(String requestSgg, int requestPort, int port,
                                    String selfMonitorIp, String selfLinkIp, String selfTargetIp,
                                    String sggMonitorIp, String sggLinkIp, String sggTargetIp, String status, String protocol)  {
        execSelfChannel(requestSgg,requestPort,port,selfMonitorIp,selfLinkIp,selfTargetIp,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol);
        execAppendSggChannel(requestSgg,requestPort,port,0,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol,Integer.toString(5*60*1000));
    }


    public static void requestData(String requestSgg, int requestPort, int startPort, int endPort, String sggMonitorIp, String sggLinkIp, String sggTargetIp, String status, String protocol,String timeOut) {
        execAppendSggChannel(requestSgg,requestPort,startPort,endPort,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol,timeOut);

    }
    private static void execAppendSggChannel(String requestSgg, int requestPort, int startPort, int endPort,
                                             String sggMonitorIp, String sggLinkIp, String sggTargetIp,
                                             String status, String protocol,String timeOut)  {
        ExternalCommend execCommend = getExecCommend(requestSgg, requestPort);
        for (int i = startPort; i <= endPort ; i++) {
            Passageway passageway = new Passageway();
            String name = "RTP-AUTO-" + i;
            passageway.setName(name);
            passageway.setType(protocol.toUpperCase());
            passageway.setPortType(true);
            passageway.setId(name);
            //流插白帧相关
            passageway.setShouldStat(0);
            passageway.setShouldFrame(0);
            List<PassagewayParam> passagewayParams = new ArrayList<>();
            PassagewayParam passagewayParam = new PassagewayParam();
            passagewayParam.setPassagewayId(name);
            passagewayParam.setMonitorPort(String.valueOf(i));
            passagewayParam.setTargetPort(String.valueOf(i));
            String sggLocalIpIn = sggMonitorIp;
            String sggLocalIpOut = sggLinkIp;
            String sggLocalIpDest = sggTargetIp;

            passagewayParam.setMonitorIp(sggLocalIpIn);
            passagewayParam.setLinkIp(sggLocalIpOut);
            passagewayParam.setTargetIp(sggLocalIpDest);
            passagewayParams.add(passagewayParam);
            passageway.setPassagewayParams(passagewayParams);
            passageway.setState(status);
            passageway.setTimeOut(Integer.parseInt(timeOut));

            RespData<Boolean> result = execCommend.startPassageway(passageway);
            System.out.println("[EXEC　CMD]{"+passageway+"}:{"+result+"}");
        }
    }
    public static ExternalCommend getExecCommend(String ip,int port) {
        if (ip != null && !ip.isEmpty()) {
            String url = "http://" + ip + ":" + port + "/externalCommend";
            HessianProxyFactory factory = new HessianProxyFactory();
            try {
                return (ExternalCommend) factory.create(ExternalCommend.class, url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @Description: 请求本地通道
     * @Param: sip请求，tcp/udp模式，当前Thread对象，passageway网闸参数
     * @Author: wangsw
     * @date:
     * @return
     * @param requestSgg
     * @param requestPort
     * @param port
     * @param selfMonitorIp
     * @param selfLinkIp
     * @param selfTargetIp
     * @param sggMonitorIp
     * @param sggLinkIp
     * @param sggTargetIp
     * @param status
     * @param protocol
     */
    private static void execSelfChannel(String requestSgg, int requestPort, int port,
                                        String selfMonitorIp, String selfLinkIp, String selfTargetIp,
                                        String sggMonitorIp, String sggLinkIp, String sggTargetIp,
                                        String status, String protocol)  {
        //#./inAppendRules.sh 协议类型 监听ip 监听端口 内端机ib 外端机ib
        //#./inAppendRules.sh tcp 172.20.52.99 8000 10.35.52.10 10.35.52.20
        //#./inAppendRules.sh 协议类型 监听ip 监听端口 内端机ib 外端机ib
        //#./inAppendRules.sh tcp 172.20.52.99 8000 10.35.52.10 10.35.52.20
        String cmd = "";
        if ("start".equalsIgnoreCase(status)) {
            cmd = " /opt/vscg/engine/sipforword/inAppendRules.sh  "
                    + protocol +" "
                    + selfMonitorIp +" "
                    + port + " "
                    + selfLinkIp +" "
                    + selfTargetIp +" "
                    + port + " ";
        }else if ("stop".equalsIgnoreCase(status)){
            cmd = " /opt/vscg/engine/sipforword/inDelRules.sh   "
                    + protocol +" "
                    + selfMonitorIp +" "
                    + port + " "
                    + selfLinkIp +" "
                    + selfTargetIp +" "
                    + port + " ";
        }

        try {
            SshExecuter.execSh(cmd);
            System.out.println("[EXEC CMD]["+protocol+"]： [{"+cmd+"}]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
