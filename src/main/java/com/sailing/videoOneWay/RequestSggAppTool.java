package com.sailing.videoOneWay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RequestSggAppTool {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dto implements Serializable {
        // 本端监听IP地址
        private String listenIp;
        // 本端监听端口
        private Integer listenPort;
        // 本端监听协议 0 UDP; 1 TCP
        private Integer listenProtocol;
        // 转发IP地址
        private String remoteIp;
        // 转发端口
        private Integer remotePort;
        // 转发协议 0 UDP; 1 TCP
        private Integer remoteProtocol;
        // 后置上行监听IP地址
        private String rearUpIp;
        // 后置上行监听端口
        private Integer rearUpPort;
        // 后置上行监听协议 0 UDP; 1 TCP
        private Integer rearUpProtocol;
        // 后置下行IP地址
        private String rearDownIp;
        // 后置下行监听端口
        private Integer rearDownPort;
        // 后置下行监听协议 0 UDP; 1 TCP
        private Integer rearDownProtocol;

    }

    private static final long codeForStart = 0x420A0008;
    private static final long resCodeOfStart = 0x420A0009;
    private static final long codeForStop = 0x420A000A;
    private static final long resCodeOfStop = 0x420A000B;
    //头部信息长度
    private static final int headLength = 6;

    public static void main(String[] args) throws Exception {
        ArrayList<Dto> dtoList = new ArrayList<>();
        Dto e = new Dto("172.20.52.61",5060,1,"172.20.52.96",5000,1,"172.20.54.61",5061,1,"172.20.52.229",6000,1
                );
        dtoList.add(e);
        startProxy(dtoList);

    }
    /**
     * 启动/停止sgg_app代理
     * @param dtoList
     * @throws Exception
     */
    public static void startProxy(List<Dto> dtoList) throws Exception {
        byte[] bytes = getContentBytesForStart(dtoList);
        sendTcpRequestToInvokeTheC(bytes, codeForStart);
    }

    public static void stopProxy(List<Dto> dtoList) {
        byte[] bytes = getContentBytesForStop(dtoList);
        sendTcpRequestToInvokeTheC(bytes, codeForStop);
    }


    private static int sendTcpRequestToInvokeTheC(byte[] contentBytes, long actionCode) {
        String tcpIp = "127.0.0.1";
        // 只有TCP，4298端口
        int port = 4399;
        String action = getAction(actionCode);
        log.info(String.format("CB is about to send request to C, action=%s", action));
        TcpSendTool tcpSend = new TcpSendTool(tcpIp, contentBytes, port);
        return tcpSend.send(action);
    }

    private static String getAction(long actionCode) {
        String action = "";
        if (actionCode == codeForStart) {
            action = "start";
        } else if (actionCode == codeForStop) {
            action = "stop";
        }
        return action;
    }


    private static byte[] getContentBytesForStart(List<Dto> dtoList) throws Exception {
        // 有几组消息体
        int bodySize = dtoList.size();
        // 每组消息体的字段个数
        int eachBodyLength = 12;
        int dataLength = headLength + (bodySize * eachBodyLength); //数据数组长度
        int mesLength = dataLength * 4;  //消息总长度
        long[] dataArray = new long[dataLength];
        dataArray = setHead(dataArray, codeForStart, mesLength);
        int index = headLength;
        for (int i = 0; i < bodySize; i++) {
            Dto dto = dtoList.get(i);
            dataArray[index] = ipToLong(dto.getListenIp()); //监听IP地址
            dataArray[index+1] = dto.getListenPort(); //监听端口
            dataArray[index+2] = dto.getListenProtocol();  //协议 0 UDP协议 1 TCP协议
            dataArray[index+3] = ipToLong(dto.getRemoteIp()); //目标地址
            dataArray[index+4] = dto.getRemotePort(); //端口号5555
            dataArray[index+5] = dto.getRemoteProtocol();  //协议 0 UDP协议 1 TCP协议
            dataArray[index+6] = ipToLong(dto.getRearUpIp());
            dataArray[index+7] = dto.getRearUpPort();
            dataArray[index+8] = dto.getRearUpProtocol();
            dataArray[index+9] = ipToLong(dto.getRearDownIp());
            dataArray[index+10] = dto.getRearDownPort();
            dataArray[index+11] = dto.getRearDownProtocol();
            index += eachBodyLength;
        }
        byte[] content = convertLongsToBytes(mesLength, dataArray);
        return content;
    }

    private static byte[] getContentBytesForStop(List<Dto> dtoList) {
        // 有几组消息体
        int bodySize = dtoList.size();
        // 每组消息体的字段个数
        int eachBodyLength = 3;
        int dataLength = headLength + (bodySize * eachBodyLength); //数据数组长度
        int mesLength = dataLength * 4;  //消息总长度
        long[] dataArray = new long[dataLength];
        dataArray = setHead(dataArray, codeForStop, mesLength);
        int index = headLength;
        for (int i = 0; i < bodySize; i++) {
            Dto dto = dtoList.get(i);
            dataArray[index] = ipToLong(dto.getListenIp()); //监听IP地址
            dataArray[index+1] = dto.getListenPort(); //监听端口
            dataArray[index+2] = dto.getListenProtocol();  //协议 0 UDP协议 1 TCP协议
            index += eachBodyLength;
        }
        byte[] content = convertLongsToBytes(mesLength, dataArray);
        return content;
    }

    private static long[] setHead(long[] dataArray, long msgType, int mesLength) {
        dataArray[0] = msgType;/* 消息类型ID */
        dataArray[1] = mesLength;/* 消息总长度，包括消息头部长度 */
        dataArray[2] = 1; /* 针对主动发送的消息是否需要对端回response响应报文，如果需要则填写1，对端发送消息时msgType=本消息发出的消息msgType+1 */
        dataArray[3] = 100; /* 消息体版本号，扩展使用，当前固定填写100 */
        dataArray[4] = 0;  /* 错误码，成功填写0 */
        dataArray[5] = 0; // 保留字段，暂时不使用，但是位置需要保留
        return dataArray;
    }

    private static long ipToLong(String ip) {
        String[] arr = ip.split("\\.");
        long result = 0;
        for (int i = 0; i <= 3; i++) {
            long ipl = Long.parseLong(arr[i]);
            result |= ipl << ((3-i) << 3);
        }
        return result;
    }

    private static byte[] convertLongsToBytes(int mesLength, long[] dataArray) {
        int offset = 0;
        byte[] content = new byte[mesLength];
        for (long n : dataArray) {
            intToBytes(n, content, offset, false);
            offset = offset + 4;
        }
        return content;
    }

    private static void intToBytes( long n, byte[] array, int offset,boolean big){
        if(big){
            array[3+offset] = (byte) (n & 0xff);
            array[2+offset] = (byte) (n >> 8 & 0xff);
            array[1+offset] = (byte) (n >> 16 & 0xff);
            array[offset] = (byte) (n >> 24 & 0xff);
        }else{
            array[offset] = (byte) (n & 0xff);
            array[1+offset] = (byte) (n >> 8 & 0xff);
            array[2+offset] = (byte) (n >> 16 & 0xff);
            array[3+offset] = (byte) (n >> 24 & 0xff);
        }
    }


}
