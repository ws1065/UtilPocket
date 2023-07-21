package com.sailing.proxy.vscgproxy;

import com.alibaba.fastjson.JSON;
import com.sailing.dscg.common.Constants;
import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.TCPSend;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.passageway.Passageway;
import com.sailing.dscg.entity.passageway.PassagewayParam;
import com.sailing.dscg.interfaces.ExecCommend;
import com.sailing.dscg.util.MathUtil;
import com.sailing.dscg.util.PassagewayUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sailing.dscg.common.Constants.STATE_START;
import static com.sailing.dscg.common.Constants.STATE_STOP;
import static org.apache.commons.lang.StringUtils.isBlank;

@Slf4j
/**
* @Description: 实现一个方法为了引擎请求本地的C通道
* @Param:
* @return:
* @Author: wangsw
* @date:
*/
public abstract class ExecCommendImplForEngine2C implements ExecCommend {
    /**
     * 开启通道
     * 暂停通道
     * 获取通道RTP流信息
     */
    public static final long codeForStart = 0x420A000C;
    public static final long codeForStop = 0x420A000E;
    public static final long codeForGettingRtpInfo = 0x420A0004;
    public static final long codeForGettingRtpVideoFlow = 0x420A0006;
    /**
     * 启动/停止通道，RTP流，视频流相关接口，封装发送给C程序的数据的头部信息长度
     */
    private static final int headLength = 6;
    private static final int extraHeadLength = 8;
    private static final Byte ipv4 = 0;
    private static final Byte ipv6 = 1;

    @Override
    public RespData<Boolean> startPassageway(Passageway passageway) {
        passageway.setState(STATE_START);
        return sggAppPassageway(passageway);
    }

    @Override
    public RespData<Boolean> stopPassageway(Passageway passageway) {
        passageway.setState(STATE_STOP);
        return sggAppPassageway(passageway);
    }

    /**
     * damon 2020-05-14
     * 新的实现方式
     * 前端，启动/停止通道，之前是创建对象，然后新生成一个json文件，把对象写到json文件中
     * 那么现在也需要创建对象，然后将对象转成byte[]，然后发送TCP请求，将对象发送出去，由服务器上的C程序接收请求，
     * 然后处理请求，但是并不会把传参的对象持久化
     *
     * @param passageway
     * @return
     */
    public RespData<Boolean> sggAppPassageway(Passageway passageway) {
        log.info("sggAppPassageway,way obj:" + JSON.toJSONString(passageway));
        RespData<Boolean> respData = new RespData<>();
        try {
            setType(passageway);
            if (Constants.UDP.equals(passageway.getType())) {
                // damon UDP类型的通道，针对传参，进行参数合法性校验
                boolean argError = false;
                StringBuffer buffer = new StringBuffer();
                int shouldStat = passageway.getShouldStat();
                if (shouldStat < 0 || shouldStat > 3) {
                    argError = true;
                    buffer.append("传参shouldStat不合法;");
                }
                int shouldFrame = passageway.getShouldFrame();
                if (!(shouldFrame == 0 || shouldFrame == 1)) {
                    argError = true;
                    buffer.append("传参shouldFrame不合法;");
                }
                if (shouldFrame == 1) {
                    // 如果开启插白帧，判断插白帧间隔，长度
                    int interval = passageway.getIntervalOfFrame();
                    if (interval < 1 || interval > 255) {
                        argError = true;
                        buffer.append("传参intervalOfFrame不合法;");
                    }
                    int length = passageway.getLengthOfFrame();
                    if (length < 1 || length > 255) {
                        argError = true;
                        buffer.append("传参lengthOfFrame不合法;");
                    }
                }
                if (argError) {
                    respData.setRespCode(RespCodeEnum.EXCEPTION);
                    respData.setReason("CB参数校验不通过：" + buffer.toString());
                    log.info("CB参数校验不通过:" + buffer.toString());
                    return respData;
                }
            }
            long startOrStop = codeForStart;
            if (STATE_STOP.equals(passageway.getState())) {
                startOrStop = codeForStop;
            }
            boolean isTcp = true;
            String protocolType = passageway.getType();
            if (!Constants.TCP.equals(protocolType)) {
                isTcp = false;
            }
            byte[] contentBytes = getContentBytes(passageway);
            PassagewayParam param = passageway.getPassagewayParams().get(0);
            String monitorPort = param.getMonitorPort();
            String[] ports = monitorPort.split(":");
            boolean isSinglePort = ports.length == 1;
            // C程序返回状态码为0，那么就一定是成功
            int successCode = 0;
            int resCode = sendTcpRequestToInvokeTheC(contentBytes, isTcp, startOrStop, passageway);
            boolean opResult = false;
            if (isSinglePort) {
                // 单个端口
                if (successCode == resCode) {
                    opResult = true;
                } else if (PassagewayUtil.doesPassagewayExist(resCode)){
                    opResult = true;
                    respData.setCode(resCode);
                    respData.setReason("请勿重复启动通道");
                } else {
                    opResult = false;
                }
            } else {
                // 端口段 只有底层返回成功的状态码，才是成功
                if (successCode == resCode) {
                    opResult = true;
                } else {
                    opResult = false;
                    // 操作失败，再判断本次是否为启动操作，如果是，那么还需要发送停止的命令，这是和单个端口的区别
                    if (startOrStop == codeForStart) {
                        log.error("start passageway failed, about to send one more request to stop passageway");
                        passageway.setState(Constants.STATE_STOP);
                        contentBytes = getContentBytes(passageway);
                        //这边去执行关闭指令，那么有可能关闭失败
                        resCode = sendTcpRequestToInvokeTheC(contentBytes, isTcp, codeForStop, passageway);
                        if (successCode == resCode) {
                            // 说明关闭通道成功，但是用户的目的是启动，此时抛出异常提示用户
                            log.error("start通道失败，其后的stop通道成功，本次启动通道操作失败");
                            throw new RuntimeException("启动通道失败");
                        } else {
                            // 这是关闭通道也失败了，那么无法启动，也无法关闭，说明C程序目前已经失去处理能力
                            log.error("start通道失败，其后的stop通道也失败，本次启动通道操作失败");
                            throw new RuntimeException("启动通道的服务存在异常，请联系管理员");
                        }
                    }
                }
            }
            // 上面，有可能会给code赋值，因此这里做null判断
            if (respData.getCode() == null) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
            }
            respData.setData(opResult);
            return respData;
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            respData.setReason("CB内部处理异常：" + e.getMessage());
            log.error("ExecCommendImpl操作通道异常：", e);
        }
        return respData;
    }

    private void setType(Passageway passageway) {
        switch (passageway.getType()) {
            case Constants.RTP:
            case Constants.SIP:
                passageway.setType(Constants.UDP);
                break;
            case Constants.HTTP:
                passageway.setType(Constants.TCP);
                break;
        }
    }

    private byte[] getContentBytes(Passageway passageway) throws Exception {
        long startOrStop = codeForStart;
        if(Constants.STATE_STOP.equalsIgnoreCase(passageway.getState())){
            startOrStop = codeForStop;
        }
        byte[] contentBytes = null;
        //目前通道管理列表页的每个对象，起通道数据，其实都只有1条，虽然现在是用了list接收通道数据，但其实只有1条数据，因此，get(0)获取第1且唯一的通道
        List<PassagewayParam> pps = passageway.getPassagewayParams();
        PassagewayParam way = pps.get(0);
        int[] listenPorts = changePortToIntArray(way.getMonitorPort());
        int[] targetPorts = new int[]{-1};
        if (way.getTargetPort() != null) {
             targetPorts = changePortToIntArray(way.getTargetPort());
        }
        //0 client, 1 server VCS项目固定为client
        int clientOrServer = 0;
        //0 UDP协议 1 TCP协议
        int udpOrTcp = 1;
        String protocolType = passageway.getType();
        if (Constants.TCP.equals(protocolType)) {
            udpOrTcp = 1;
        } else if (Constants.UDP.equals(protocolType)) {
            udpOrTcp = 0;
        }
        contentBytes = tcpSend(startOrStop, clientOrServer, way.getMonitorIp(), listenPorts, udpOrTcp,
                way.getTargetIp(), targetPorts, udpOrTcp,
                way.getPostIp(), way.getPostPort(), udpOrTcp, passageway);
        return contentBytes;
    }

    /**
     * 发送TCP请求，调用C程序，去启动进程
     * 这个CB程序，和C程序，其实是部署在同一台机器上的，因此，发送TCP请求的地址为本地
     * 有个坑需要注意，尤其是开发环境，如果tcp和UDP的json文件所配置的IP地址，是服务器的ip地址，而不是 127.0.0.1，那么，下面的 tcpIp也要写IP地址
     * 然后是端口，TCP类型的通道，需要调用TCP进程的C程序，UDP类型的通道，调用UDP进程的C程序，二者的端口是不一样的
     * @param contentBytes
     */
    private int sendTcpRequestToInvokeTheC(byte[] contentBytes, boolean isTcpType, long actionCode, Passageway passageway) {
        String tcpIp = "192.168.2.132";
        int port = getPort();
        String action = getAction(actionCode);
        log.info(String.format("CB is about to send request to C, action=%s, passageway name=%s, monitor port=%s, udpOrTcp:%s",
                action, passageway.getName(), passageway.getPassagewayParams().get(0).getMonitorPort(), passageway.getType()));
        TCPSend tcpSend = new TCPSend(tcpIp, contentBytes, port);
        return tcpSend.send(action);
    }
    private String getAction(long actionCode) {
        String action = "";
        if (actionCode == codeForStart) {
            action = "start";
        } else if (actionCode == codeForStop) {
            action = "stop";
        } else if (actionCode == codeForGettingRtpInfo) {
            action = "getRtpInfo";
        } else if (actionCode == codeForGettingRtpVideoFlow) {
            action = "getRtpVideoFlow";
        }
        return action;
    }
    private Object sendRequestToCToGetRtpInfo(byte[] contentBytes, boolean isTcpType, long actionCode, String logInfo) {
        String ip = "127.0.0.1";
        int port = getPort();
        String action = getAction(actionCode);
        if (logInfo != null) {
            log.info(String.format("CB is about to send request to C for getting RTP info, action=%s, args=[%s]", action, logInfo));
        } else {
            log.info(String.format("CB is about to send request to C for getting RTP info, action=%s, no arg", action));
        }
        TCPSend tcpSend = new TCPSend(ip, contentBytes, port);
        return tcpSend.getRtpInfo(action);
    }

    private int getPort() {
        // vscg_app服务端口只有一个
        return 4400;
    }

    /**
     * 注意，这是把端口段 50001:50009，封装为端口数组[50001, 50009]，数组只有2个元素
     */
    public static int[] changePortToIntArray(String port) {
        String[] ports = port.split(":");
        List<Integer> temp = new ArrayList<>();
        for (String p: ports) {
            if (isBlank(p)) {
                continue;
            }
            temp.add(Integer.valueOf(p));
        }
        int[] listenPorts = new int[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            listenPorts[i] = temp.get(i);
        }
        return listenPorts;
    }

    // 本地测试
/*    public void localTest() {
        try {
            String listenIp = "172.20.54.75";
            String port = "17000:17005";
            Passageway passageway = new Passageway();
            passageway.setName("damon本地测试通道");
            passageway.setType(UDP);
            passageway.setPortType(true);
            passageway.setShouldStat(0);
            passageway.setShouldFrame(0);
            passageway.setLengthOfFrame(0);
            passageway.setIntervalOfFrame(0);
            PassagewayParam param = new PassagewayParam();
            param.setMonitorIp(listenIp);
            param.setLinkIp(listenIp);
            param.setMonitorPort(port);
            param.setTargetIp("172.20.52.105");
            param.setTargetPort(port);
            List<PassagewayParam> list = new ArrayList<>();
            list.add(param);
            passageway.setPassagewayParams(list);
            RespData<Boolean> respData = stopPassageway(passageway);
            log.info("结果=" + respData);
        } catch (Exception e) {
            log.error("本地测试异常,msg:" + e.getMessage(), e);
        }
    }*/

    private static long[] setHead(long[] dataArray, long msgType, int mesLength) {
        dataArray[0] = msgType;/* 消息类型ID */
        dataArray[1] = mesLength;/* 消息总长度，包括消息头部长度 */
        dataArray[2] = 1; /* 针对主动发送的消息是否需要对端回response响应报文，如果需要则填写1，对端发送消息时msgType=本消息发出的消息msgType+1 */
        dataArray[3] = 100; /* 消息体版本号，扩展使用，当前固定填写100 */
        dataArray[4] = 0;  /* 错误码，成功填写0 */
        dataArray[5] = 0; // 保留字段，暂时不使用，但是位置需要保留
        return dataArray;
    }

    // 支持通道差异化的版本，在启动通道的时候，还需要设置额外的消息头
    private static long[] setExtraHeadForStartPassageway(long[] dataArray, Passageway passageway) {
        int start = headLength;
        dataArray[start] = passageway.getClientOrServer(); // 0客户端，1服务端，由VSCG的引擎传参
        dataArray[start+1] = passageway.getUdpSendInterval();
        dataArray[start+2] = passageway.getUdpSendHeartBeat() == null ? 0 : passageway.getUdpSendHeartBeat();
        dataArray[start+3] = passageway.getTcpBuffMaxCount() == null ? 128 : passageway.getTcpBuffMaxCount();
        dataArray[start+4] = passageway.getUdpBuffMaxCount() == null ? 128 : passageway.getUdpBuffMaxCount();
        dataArray[start+5] = passageway.getSggMultiLines() == null ? 5 : passageway.getSggMultiLines();
        dataArray[start+6] = passageway.getSggTcpTimeout() == null ? 0 : passageway.getSggTcpTimeout();
        dataArray[start+7] = passageway.getTimeOut();  // 通道的超时时间，默认是0
        return dataArray;
    }

    //这个是支持通道差异化的版本
    private static byte[] tcpSend(long msgType, int location, String listenIp, int[] listenPorts, int listenType,
                                  String remoteIp, int[] remotePorts, int remoteType,
                                  String postIp, int postPort, int postType, Passageway passageway) throws Exception {

        int minListenPort = listenPorts[0]; //最小监听端口
        int maxListenPort;
        if (listenPorts.length > 1) {
            maxListenPort = listenPorts[1]; //最大监听端口
        } else {
            maxListenPort = listenPorts[0];
        }
        int minRemotePort = remotePorts[0]; //最大目标端口
        int portNum = maxListenPort-minListenPort+1; // 端口数量
        // n + (portNum * x) n是for循环上面的元素个数，x是for 循环中，元素个数
        //头部信息长度
        int totalHeadLength;
        boolean isStart = Constants.STATE_START.equals(passageway.getState());
        // 停止通道的时候，不需要setExtraHeadForStartPassageway
        if (isStart) {
            totalHeadLength = headLength + extraHeadLength; // 目前长度14
        } else {
            totalHeadLength = headLength;
        }
        InnerArgClass argClass = new InnerArgClass(msgType, location, listenIp, listenPorts, listenType,
                remoteIp, remotePorts, remoteType,  postIp, postPort, postType,
                passageway, minRemotePort, portNum, minListenPort, maxListenPort, isStart, totalHeadLength);
        /**
         * damon 2021-07-19 目前这个版本的C程序不支持IPV6
         */
        byte[] content = getContentBytesNotInIpv6Way(argClass);
        return content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class InnerArgClass implements Serializable {

        public long msgType;
        public int location;
        public String listenIp;
        public int[] listenPorts;
        public int listenType;
        public String remoteIp;
        public int[] remotePorts;
        public int remoteType;
        /**
         * 仅针对VSCG
         * 后置机IP
         * 可以为空，因为只有在client端，该字段才有效
         */
        private String postIp;

        /**
         * 后置机端口
         * 在client端才有效，非client端，那么默认0
         */
        private int postPort;

        /**
         * 后置机协议 0:UDP, 1:TCP
         * 在client端有效
         */
        private int postProtocol;
        public Passageway passageway;
        public int minRemotePort;
        public int portNum;
        public int minListenPort;
        public int maxListenPort;
        public boolean isStart;
        public int totalHeadLength;
    }

    private static byte[] getContentBytesNotInIpv6Way(InnerArgClass arg) {
        //消息体，每个对象的长度
        int eachBodyLength = 10;
        int dataLength = arg.totalHeadLength + (arg.portNum * eachBodyLength); //数据数组长度
        int mesLength = dataLength * 4;  //消息总长度
        long[] dataArray = new long[dataLength];
        long rtpParam = getRtpParam(arg.passageway);
        dataArray = setHead(dataArray, arg.msgType, mesLength);
        if (arg.isStart) {
            dataArray = setExtraHeadForStartPassageway(dataArray, arg.passageway);
        }
        int index = arg.totalHeadLength;
        for (int port = arg.minListenPort; port <= arg.maxListenPort ; port++) {
            dataArray[index] = ipToLong(arg.listenIp); //监听IP地址
            dataArray[index+1] = port; //监听端口
            dataArray[index+2] = arg.listenType;  //协议 0 UDP协议 1 TCP协议
            dataArray[index+3] = ipToLong(arg.remoteIp); //目标地址
            dataArray[index+4] = arg.minRemotePort; //端口号5555
            dataArray[index+5] = arg.remoteType;  //协议 0 UDP协议 1 TCP协议
            dataArray[index+6] = rtpParam;
            dataArray[index+7] = ipToLong(arg.getPostIp()); // 后置机ip，location为client才有效
            dataArray[index+8] = arg.getPostPort(); // 后置机端口，location为client才有效
            dataArray[index+9] = arg.getPostProtocol(); // 后置机协议，location为client才有效，0是UDP,1是TCP
            arg.minRemotePort += 1;
            index += eachBodyLength;
        }
//        log.info("dataArray is: " + Arrays.toString(dataArray));
        byte[] content = convertLongsToBytes(mesLength, dataArray);
        return content;
    }

    /**
     * 4个字段，用于接收4个字节的参数，
     * 每个字段都是int值，例如 1 1 100 255
     * 那么从高位到低位就是 255 100 1 1
     * 将每个数字转成十六进制的字符串，255 => FF, 100 => 64, 1 => 01, 1 => 01
     * 进行拼接 FF640101
     * 其实 FF640101就可以了，但问题是，此时的FF640101是字符串，我传给C的值，是数字，所以将该字符串转成数字
     * @param passageway
     * @return
     */
    private static long getRtpParam(Passageway passageway) {
        long rtpParam = passageway.getRtpParam();
        if (rtpParam != 0) {
            //为了灵活点，支持rtpParam直接传参，这样就不去读取另外的4个字段了
            return rtpParam;
        }
        String prefix = "0";
        //int转为十六进制，intToHex，如果大于15，那么会返回两个字符
        String first = MathUtil.intToHex(passageway.getLengthOfFrame());
        // 小于等于15，那么只返回一个数字，那么需要在前面拼接一个0
        if (first.length() == 1) {
            first = prefix + first;
        }
        String second = MathUtil.intToHex(passageway.getIntervalOfFrame());
        if (second.length() == 1) {
            second = prefix + second;
        }
        String third = MathUtil.intToHex(passageway.getShouldFrame());
        //第3,4位，要求的效果是 01,00
        third = prefix + third;
        String fourth = MathUtil.intToHex(passageway.getShouldStat());
        fourth = prefix + fourth;
        String append = first + second + third + fourth;
        log.info("append result for rtp param=" + append);
        rtpParam = MathUtil.hexToDecimal(append);
        if (rtpParam == 0) {
            rtpParam = 0X00000000;
        }
        log.info("rtp param=" + rtpParam);
        return rtpParam;
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

    /**
     * java 为大端byte
     * C 为小端byte
     * @param n
     * @param array
     * @param offset
     * @param big
     */
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



    private static long ipToLong(String ip) {
        // damon 进行优化，有些情况下，某些IP可以为空，但是传参给C还是需要传值的，传参0即可
        if (StringUtils.isBlank(ip)) {
            return 0;
        }
        String[] arr = ip.split("\\.");
        long result = 0;
        for (int i = 0; i <= 3; i++) {
            long ipl = Long.parseLong(arr[i]);
            result |= ipl << ((3-i) << 3);
        }
        return result;
    }

    /**
     * 网上找的
     * 这个方法和本类中 intToBytes,传参big为false是呼应的
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序
     * @param src byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value =  ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }




}
