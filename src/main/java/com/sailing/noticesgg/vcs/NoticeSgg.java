package com.sailing.noticesgg.vcs;


import com.caucho.hessian.client.HessianProxyFactory;
import com.sailing.SshExecuter;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.configManage.ServiceStatus;
import com.sailing.dscg.entity.configManage.SipForwordConfig;
import com.sailing.dscg.entity.logManage.LogSystemAlarm;
import com.sailing.dscg.entity.monitors.SystemResource;
import com.sailing.dscg.entity.networkCard.NetworkCard;
import com.sailing.dscg.entity.passageway.Passageway;
import com.sailing.dscg.entity.passageway.PassagewayParam;
import com.sailing.dscg.entity.passageway.dto.PassagewayRtpVideoFlowArgDto;
import com.sailing.dscg.entity.passageway.vo.PassagewayRtpInfoVO;
import com.sailing.dscg.entity.passageway.vo.PassagewayRtpVideoFlowInfoVO;
import com.sailing.dscg.entity.system.PingTest;
import com.sailing.dscg.interfaces.ExecCommend;
import com.sailing.dscg.interfaces.ExternalCommend;
import com.sailing.dscg.interfaces.impl.ExecCommendImplForEngine2C;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 请求网闸开放端口，并且返回相应的状态
 * @author :wsw
 */
public class NoticeSgg {



    public static void requestDataVCS(int startPort, int endPort, String sggMonitorIp,
                                      String sggLinkIp, String sggTargetIp, String status, String protocol,
                                      String timeout) {
        execAppendSggChannel(startPort,endPort,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol,timeout);
    }
    //向cb请求数据
    public static  void requestData(String requestSgg, int requestPort, int port,
                                    String selfMonitorIp, String selfLinkIp, String selfTargetIp,
                                    String sggMonitorIp, String sggLinkIp, String sggTargetIp, String status, String protocol)  {
        //execSelfChannel(requestSgg,requestPort,port,selfMonitorIp,selfLinkIp,selfTargetIp,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol);
       execAppendSggChannel(port,0,sggMonitorIp,sggLinkIp,sggTargetIp,status,protocol,Integer.toString(5*60*1000));
    }


    public static void main(String[] args) throws Exception {
        requestData(45000,45300,"172.20.52.94","172.20.54.240","10.35.52.20","start","udp","10");
    }



    public static void requestData( int startPort, int endPort, String sggMonitorIp, String sggPostIp, String sggTargetIp, String status, String protocol,String timeOut) {
        execAppendSggChannel(startPort,endPort,sggMonitorIp,sggPostIp,sggTargetIp,status,protocol,timeOut);

    }
    private static void execAppendSggChannel(int startPort, int endPort,
                                             String sggMonitorIp, String sggPostIp, String sggTargetIp,
                                             String status, String protocol,String timeOut)  {
        ExecCommend execCommendImplForEngine2C = new ExecCommendImplForEngine2C() {
            @Override
            public RespData<Boolean> syncZk(String zk) {
                return null;
            }

            @Override
            public RespData<ServiceStatus> queryServiceStatus(String serviceId, String realTargetIp) {
                return null;
            }

            @Override
            public RespData<Boolean> start(SipForwordConfig sipForwordConfig, String realTargetIp) {
                return null;
            }

            @Override
            public RespData<Boolean> deploy(SipForwordConfig sipForwordConfig, String realTargetIp) {
                return null;
            }

            @Override
            public RespData<Boolean> stop(SipForwordConfig sipForwordConfig, String realTargetIp) {
                return null;
            }

            @Override
            public RespData<Boolean> delete(SipForwordConfig sipForwordConfig, String realTargetIp) {
                return null;
            }

            @Override
            public RespData<Boolean> deploySCMSCommends(String status) {
                return null;
            }

            @Override
            public RespData<Boolean> deployNetworkCard(NetworkCard networkCard) {
                return null;
            }

            @Override
            public RespData<Boolean> uptSysConfig(NetworkCard networkCard) {
                return null;
            }

            @Override
            public RespData<List<NetworkCard>> queryList(NetworkCard networkCard) {
                return null;
            }

            @Override
            public RespData<NetworkCard> getManagerPort() {
                return null;
            }

            @Override
            public RespData<Boolean> updateTaskProperties(Map<String, HashMap<String, String>> map) {
                return null;
            }

            @Override
            public RespData<Boolean> restartSys() {
                return null;
            }

            @Override
            public RespData<Boolean> halt() {
                return null;
            }

            @Override
            public RespData<Boolean> allowPing(Boolean allowPing) {
                return null;
            }

            @Override
            public RespData<Boolean> pingTest(PingTest pingTest) {
                return null;
            }

            @Override
            public RespData<String> pingTestBackText() {
                return null;
            }

            @Override
            public RespData<Boolean> pingTestStop() {
                return null;
            }

            @Override
            public RespData<Boolean> setSsh(Boolean sshEnable) {
                return null;
            }

            @Override
            public RespData<SystemResource> getSystemResource() {
                return null;
            }

            @Override
            public RespData<Map<String, Object>> queryLogSystemAlarmList(LogSystemAlarm logSystemAlarm) {
                return null;
            }

            @Override
            public RespData<Integer> queryNoReadSystemAlarmIpCount(String ip) {
                return null;
            }

            @Override
            public RespData<Boolean> updateAlarmRead(String ip) {
                return null;
            }

            @Override
            public RespData<List<PassagewayRtpInfoVO>> getRtpInfoOfPassageway() {
                return null;
            }

            @Override
            public RespData<List<PassagewayRtpVideoFlowInfoVO>> getRtpVideoFlowInfo(PassagewayRtpVideoFlowArgDto dto) {
                return null;
            }
        };
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
            passageway.setCallId("callid");
            List<PassagewayParam> passagewayParams = new ArrayList<>();
            PassagewayParam passagewayParam = new PassagewayParam();
            passagewayParam.setPassagewayId(name);

            passagewayParam.setMonitorPort(String.valueOf(i));
            passagewayParam.setPostProtocol(0);
            passagewayParam.setTargetPort(String.valueOf(14444));
            passagewayParam.setPostPort(i);

            passagewayParam.setMonitorIp(sggMonitorIp);
            passagewayParam.setTargetIp(sggTargetIp);
            passagewayParam.setPostIp(sggPostIp);
            passagewayParams.add(passagewayParam);
            passageway.setPassagewayParams(passagewayParams);
            passageway.setTimeOut(0);

            if ("start".equalsIgnoreCase(status)) {
                passageway.setState(status);
                RespData<Boolean> result = execCommendImplForEngine2C.startPassageway(passageway);

            } else if ("stop".equalsIgnoreCase(status)) {
                passageway.setState(status);
                RespData<Boolean> result = execCommendImplForEngine2C.stopPassageway(passageway);

                System.out.println("[EXEC　CMD]{"+passageway+"}:{"+result+"}");
            }
        }
    }
    public static ExternalCommend getExecCommend(String ip,int port) {
        if (ip != null && !ip.isEmpty()) {
            String url = "http://" + ip + ":" + port + "/ExecCommend";
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
