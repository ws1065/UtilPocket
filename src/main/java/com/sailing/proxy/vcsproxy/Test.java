package com.sailing.proxy.vcsproxy;

import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.passageway.Passageway;
import com.sailing.dscg.entity.passageway.PassagewayParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-09-14 00:13
 */
public class Test {
    public static void main(String[] args) {

        String id = "RTP-AUTO-CB-create-" + 1;
        Passageway passageway = new Passageway();
        passageway.setId(id);
        passageway.setDirection(null);
        passageway.setName(id);
        passageway.setType( "TCP");
        passageway.setPortType(true);
        passageway.setTcpBuffMaxCount(128);
        passageway.setSggMultiLines(500);
        passageway.setState("start");
        //passageway.setClientOrServer(isClient ? 0 : 1);

        ExecCommendImpl exec = new ExecCommendImpl();

        List<PassagewayParam> paramList = new ArrayList<>();
        PassagewayParam passagewayParams = new PassagewayParam();
        paramList.add(passagewayParams);
        passageway.setPassagewayParams(paramList);

        passagewayParams.setMonitorIp("192.168.91.131");
        passagewayParams.setLinkIp("192.168.2.130");
        passagewayParams.setTargetIp("192.168.2.211");
        passagewayParams.setMonitorPort("1935");
        passagewayParams.setTargetPort("1935");
        RespData<Boolean> booleanRespData = exec.startPassageway(passageway);
        System.out.println();
    }
}