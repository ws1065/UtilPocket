package com.sailing.oneWayApp;

import com.sailing.dscg.common.RequestSggAppTool;

import java.util.ArrayList;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-10-27 14:24
 */
public class OneWayTest {
    public static void main(String[] args) throws Exception {
        int protocols = 0;
        ArrayList<RequestSggAppTool.Dto> dtoList = new ArrayList<>();
//        Dto e = new Dto("172.20.52.130", 5060, protocol, "172.20.52.99", 18000,
//                "172.20.52.96", 5000, protocol, "172.20.54.99", 20000,
//                "172.20.54.61", 5061, protocol, "172.20.52.130", 21000,
//                "172.20.52.229",6000, protocol
//                );
        RequestSggAppTool.Dto dto = new RequestSggAppTool.Dto();
        dto.setListenIp("192.168.2.131");
        dto.setListenPort(5566);
        dto.setListenProtocol(protocols);

        dto.setRemoteIp("192.168.91.130");
        dto.setRemotePort(14444);
        dto.setRemoteProtocol(protocols);

        dto.setRearUpIp("192.168.2.132");
        dto.setRearUpPort(5566);
        dto.setRearUpProtocol(protocols);

        dto.setRearUpLport(9988);

        dto.setRearDownIp("192.168.91.131");
        dto.setRearDownPort(14444);
        dto.setRearDownProtocol(protocols);

        dtoList.add(dto);
//        startProxy(dtoList);
//        stopProxy(dtoList);
    }
}