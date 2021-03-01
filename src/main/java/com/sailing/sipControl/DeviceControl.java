package com.sailing.sipControl;

/**
 * @program: vscg-platform
 * @description: 没有应答的设备控制
 * @author: wangsw
 * @create: 2020-06-18 18:29
 */
public class DeviceControl {
    //目的设备编码
    private String dstDeviceId;
    private String dstHost;
    private String dstPort;
    private String localHost;
    private String localDeviceId;

    public DeviceControl(String dstDeviceId, String dstHost, String dstPort, String localHost, String localDeviceId) {
        this.dstDeviceId = dstDeviceId;
        this.dstHost = dstHost;
        this.dstPort = dstPort;
        this.localHost = localHost;
        this.localDeviceId = localDeviceId;
        this.request = "MESSAGE sip:"+ dstDeviceId +"@"+dstHost+":"+dstPort+"SIP/2.0\n" +
                "To: sip:"+ dstDeviceId +"@"+dstHost+":"+dstPort+"\n" +
                "CSeq: 1 MESSAGE\n" +
                "Call-ID: wlss-f304376c-264c72115264900e8257eca1e15b0ae3@172.18.16.5\n" +
                "Via: SIP/2.0/UDP "+localHost+"\n" +
                "From: < sip:"+localDeviceId+"@"+localHost+">;tag=237f57dc\n" +
                "Content-Type: Application/MANSCDP+xml\n" +
                "Max-Forwards: 69\n" +
                "Content-Length: 164\n" +
                "\n" +
                "{\n" +
                "  \"Control\": {\n" +
                "    \"CmdType\": \"DeviceControl\",\n" +
                "    \"SN\": \"11\",\n" +
                "    \"DeviceID\": \""+ dstDeviceId +"\",\n" +
                "    \"PTZCmd\": \"A50F4D1000001021\",\n" +
                "    \"Info\": { \"ControlPriority\": \"5\" }\n" +
                "  }\n" +
                "}" +
                "";
    }

    public String getRequest() {
        return request;
    }

    private String request;
    String response = "SIP/2.0 200 OK\n" +
            "Via: SIP/2.0/UDP源域名或IP地址\n" +
            "From: <sip:源设备编码@源域名>;tag=237f57dc\n" +
            "To: <sip:目的设备编码@目的域名>;tag=13057\n" +
            "Call-ID: wlss-f304376c-264c72115264900e8257eca1e15b0ae3@172.18.16.5\n" +
            "CSeq: 1 MESSAGE\n" +
            "Content-Length: 0\n";
}