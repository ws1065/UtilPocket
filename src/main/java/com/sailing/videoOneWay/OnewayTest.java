package com.sailing.videoOneWay;

import java.util.Collections;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-01 18:05
 */
public class OnewayTest {

    public static void run(String[] args) throws Exception {
        RequestSggAppTool.Dto dto = new RequestSggAppTool.Dto();

        String listenIp = args[2];
        int listenPort = Integer.parseInt(args[3]);
        String remoteIp = args[4];
        int remotePort = Integer.parseInt(args[5]);
        String rearUpIp = args[6];
        int rearUpPort = Integer.parseInt(args[7]);
        String rearDownIp = args[8];
        int rearDownPort = Integer.parseInt(args[9]);

        dto.setListenIp(listenIp);
        dto.setListenPort(listenPort);
        dto.setListenProtocol(1);

        dto.setRemoteIp(remoteIp);
        dto.setRemotePort(remotePort);
        dto.setRemoteProtocol(1);

        dto.setRearUpIp(rearUpIp);
        dto.setRearUpPort(rearUpPort);
        dto.setRearUpProtocol(1);

        dto.setRearDownIp(rearDownIp);
        dto.setRearDownPort(rearDownPort);
        dto.setRearDownProtocol(1);
        
        if ("start".equals(args[1])){
            RequestSggAppTool.startProxy(Collections.singletonList(dto));
        }
        if ("stop".equals(args[1])){
            RequestSggAppTool.stopProxy(Collections.singletonList(dto));
        }

    }
}