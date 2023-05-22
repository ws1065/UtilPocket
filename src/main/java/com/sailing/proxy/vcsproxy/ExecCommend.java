package com.sailing.proxy.vcsproxy;


import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.monitor.homepage.HomePage;
import com.sailing.dscg.entity.monitor.homepage.NetworkCardFlow;
import com.sailing.dscg.entity.networkCard.NetworkCard;
import com.sailing.dscg.entity.passageway.Passageway;
import com.sailing.dscg.entity.passageway.dto.PassagewayRtpVideoFlowArgDto;
import com.sailing.dscg.entity.passageway.vo.PassagewayRtpInfoVO;
import com.sailing.dscg.entity.passageway.vo.PassagewayRtpVideoFlowInfoVO;
import com.sailing.dscg.entity.resourceManage.FileResourceConfig;
import com.sailing.dscg.entity.resourceManage.FileSwapConfig;
import com.sailing.dscg.entity.system.PingTest;
import com.sailing.dscg.entity.system.WitList;

import java.util.List;

/**
 * 针对sgg网闸的接口
 */
public interface ExecCommend {

    //网卡配置
    RespData<Boolean> deployNetworkCard(NetworkCard networkCard);

    RespData<Boolean> uptSysConfig(NetworkCard networkCard);

    RespData<List<NetworkCard>> queryNetworkCards();

    /***
     * 获取正常的网卡
     * @return
     */
    RespData<List<NetworkCard>> queryRuningNetWorkCards();

    RespData<NetworkCard> getManagerPort();
    //系统资源调度

    RespData<Boolean> startPassageway(Passageway passageway);

    RespData<Boolean> stopPassageway(Passageway passageway);


    RespData<Boolean> startIpTablesPassageway(Passageway passageway,String protocol);

    RespData<Boolean> stopIptablesPassageway(Passageway passageway,String protocol);

    RespData<Boolean> operationFTP(FileSwapConfig fileSwapConfig);

    RespData<Boolean> deleteFTP(String id);

    RespData<List<String>> getFtpFolder(FileResourceConfig fileResourceConfig);

    RespData<Boolean> testFTPConnect(FileResourceConfig fileResourceConfig);

    RespData<Boolean> addOrDelFtpIptables(String ip, String type);

    RespData<Boolean> execCmd(List<String> cmds);

    RespData<String> logWarning();

    /**
     * 重启服务器
     *
     * @return
     */
    RespData<Boolean> restartSys();

    /**
     * 关闭服务器
     *
     * @return
     */
    RespData<Boolean> halt();

    /**
     * 项目升级
     *
     * @return
     */
    RespData<Boolean> updateSystem(String absolutePath);

    /**
     * 是否允许外部ping本服务器
     *
     * @return
     */
    RespData<Boolean> allowPing(Boolean allowPing);

    /**
     * ping测试并返回结果
     *
     * @return
     */
    RespData<Boolean> pingTest(PingTest pingTest);

    /**
     * ping测试返回内容
     *
     * @return
     */
    RespData<String> pingTestBackText();

    /**
     * 停止ping测试
     *
     * @return
     */
    RespData<Boolean> pingTestStop();

    /**
     * ssh开关
     *
     * @return
     */
    RespData<Boolean> setSsh(Boolean sshEnable);

    /**
     * 视频网闸开关
     *
     * @return
     */
    RespData<Boolean> setVideoEnable(Boolean videoEnable);

    /**
     * damon 2020-08-11
     * 关闭定时任务
     * @return
     */
    RespData<Boolean> stopScheduleTaskOfInspectingPorts();

    /**
     * damon 2020-06-02
     * 恢复定时任务
     */
    RespData<Boolean> restoreScheduleTaskOfInspectingPorts();

    /**
     * 获取RTP流数据
     */
    RespData<List<PassagewayRtpInfoVO>> getRtpInfoOfPassageway();

    RespData<Boolean> saveWhiteList(Integer count, WitList witList);

    RespData<Boolean> deleteWhiteList(Integer count, WitList witList);

    RespData<HomePage> homepage(Byte timeButton, Byte moduleType);

    RespData<NetworkCardFlow> getNetcardFlowData(Byte timeButton);

    /**
     * 获取RTP视频流数据
     * 支持传参
     */
    RespData<List<PassagewayRtpVideoFlowInfoVO>> getRtpVideoFlowInfo(PassagewayRtpVideoFlowArgDto dto);

    RespData<Boolean> checkStatusOfNetworkCardOfThisPassageway(Passageway passageway);

}
