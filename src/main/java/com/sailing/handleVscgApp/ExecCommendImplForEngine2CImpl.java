//package com.sailing.handleVscgApp;
//
//import com.sailing.dscg.entity.RespData;
//import com.sailing.dscg.entity.configManage.ServiceStatus;
//import com.sailing.dscg.entity.configManage.SipForwordConfig;
//import com.sailing.dscg.entity.logManage.LogSystemAlarm;
//import com.sailing.dscg.entity.monitor.SystemResource;
//import com.sailing.dscg.entity.networkCard.NetworkCard;
//import com.sailing.dscg.entity.passageway.dto.PassagewayRtpVideoFlowArgDto;
//import com.sailing.dscg.entity.passageway.vo.PassagewayRtpInfoVO;
//import com.sailing.dscg.entity.passageway.vo.PassagewayRtpVideoFlowInfoVO;
//import com.sailing.dscg.entity.system.PingTest;
//import com.sailing.dscg.interfaces.impl.ExecCommendImplForEngine2C;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @program: VSCG
// * @description: 请求本地通道的实现类
// * @author: wangsw
// * @create: 2021-08-03 09:59
// */
//public class ExecCommendImplForEngine2CImpl extends ExecCommendImplForEngine2C {
//
//    @Override
//    public RespData<Boolean> syncZk(String zk) {
//        return null;
//    }
//
//    @Override
//    public RespData<ServiceStatus> queryServiceStatus(String serviceId, String realTargetIp) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> start(SipForwordConfig sipForwordConfig, String realTargetIp) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> deploy(SipForwordConfig sipForwordConfig, String realTargetIp) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> stop(SipForwordConfig sipForwordConfig, String realTargetIp) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> delete(SipForwordConfig sipForwordConfig, String realTargetIp) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> deploySCMSCommends(String status) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> deployNetworkCard(NetworkCard networkCard) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> uptSysConfig(NetworkCard networkCard) {
//        return null;
//    }
//
//    @Override
//    public RespData<List<NetworkCard>> queryList(NetworkCard networkCard) {
//        return null;
//    }
//
//    @Override
//    public RespData<NetworkCard> getManagerPort() {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> restartSys() {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> halt() {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> allowPing(Boolean allowPing) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> pingTest(PingTest pingTest) {
//        return null;
//    }
//
//    @Override
//    public RespData<String> pingTestBackText() {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> pingTestStop() {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> setSsh(Boolean sshEnable) {
//        return null;
//    }
//
//    @Override
//    public RespData<SystemResource> getSystemResource() {
//        return null;
//    }
//
//    @Override
//    public RespData<Map<String, Object>> queryLogSystemAlarmList(LogSystemAlarm logSystemAlarm) {
//        return null;
//    }
//
//    @Override
//    public RespData<Integer> queryNoReadSystemAlarmIpCount(String ip) {
//        return null;
//    }
//
//    @Override
//    public RespData<Boolean> updateAlarmRead(String ip) {
//        return null;
//    }
//
//    @Override
//    public RespData<List<PassagewayRtpInfoVO>> getRtpInfoOfPassageway() {
//        return null;
//    }
//
//    @Override
//    public RespData<List<PassagewayRtpVideoFlowInfoVO>> getRtpVideoFlowInfo(PassagewayRtpVideoFlowArgDto dto) {
//        return null;
//    }
//}