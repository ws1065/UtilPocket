package com.sailing.sipControl;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-08-21 10:05
 */
public class SIPControl {


    public static void NotAllow(String dstDeviceId, String dstHost, String dstPort, String localHost, String localDeviceId) {
        DeviceControl deviceControl = new DeviceControl(dstDeviceId, dstHost, dstPort, localHost, localDeviceId);
        String request = deviceControl.getRequest();
        Util.udpSend(dstHost,dstPort,request);
    }
}