//package com.sailing.noticesgg.vcs;
//
//import com.alibaba.fastjson.JSON;
//import com.sailing.dscg.common.Constants;
//import com.sailing.dscg.common.RespCodeEnum;
//import com.sailing.dscg.common.TCPSend;
//import com.sailing.dscg.entity.RespData;
//import com.sailing.dscg.entity.configManage.ServiceStatus;
//import com.sailing.dscg.entity.configManage.SipForwordConfig;
//import com.sailing.dscg.entity.logManage.LogSystemAlarm;
//import com.sailing.dscg.entity.monitor.SystemResource;
//import com.sailing.dscg.entity.networkCard.NetworkCard;
//import com.sailing.dscg.entity.passageway.Passageway;
//import com.sailing.dscg.entity.passageway.PassagewayParam;
//import com.sailing.dscg.entity.passageway.dto.ElementsOfDto;
//import com.sailing.dscg.entity.passageway.dto.PassagewayRtpVideoFlowArgDto;
//import com.sailing.dscg.entity.passageway.vo.PassagewayRtpInfoVO;
//import com.sailing.dscg.entity.passageway.vo.PassagewayRtpVideoFlowInfoVO;
//import com.sailing.dscg.entity.system.PingTest;
//import com.sailing.dscg.interfaces.ExecCommend;
//import com.sailing.dscg.util.CollectionUtil;
//import com.sailing.dscg.util.MathUtil;
//import inet.ipaddr.AddressStringException;
//import inet.ipaddr.IPAddress;
//import inet.ipaddr.IPAddressString;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.sailing.dscg.common.Constants.STATE_START;
//import static com.sailing.dscg.common.Constants.STATE_STOP;
//import static com.sailing.dscg.util.CollectionUtil.ifCollectionIsNotEmpty;
//import static org.apache.commons.lang.StringUtils.isBlank;
//
//@Slf4j
//public class ExecCommendImpl  implements ExecCommend {
//
//
//    /**
//     * 开启通道
//     * 暂停通道
//     * 获取通道RTP流信息
//     */
//    public static final long codeForStart = 0x420A0000;
//    public static final long codeForStop = 0x420A0002;
//    public static final long codeForGettingRtpInfo = 0x420A0004;
//    public static final long codeForGettingRtpVideoFlow = 0x420A0006;
//
//    /**
//     * 启动/停止通道，RTP流，视频流相关接口，封装发送给C程序的数据的头部信息长度
//     */
//    private static final int headLength = 6;
//    private static final int extraHeadLength = 8;
//    private static final Byte ipv4 = 0;
//    private static final Byte ipv6 = 1;
//
////    @Autowired
////    private ISysConfigDao sysConfigDao;
////    @Autowired
////    private ILogSystemAlarmDao logSystemAlarmDao;
//
//
//
//    @Override
//    public RespData<Boolean> startPassageway(Passageway passageway) {
//        passageway.setState(STATE_START);
//        return sggAppPassageway(passageway);
//    }
//
//    @Override
//    public RespData<Boolean> stopPassageway(Passageway passageway) {
//        passageway.setState(STATE_STOP);
//        return sggAppPassageway(passageway);
//    }
//
//    @Override
//    public RespData<List<PassagewayRtpInfoVO>> getRtpInfoOfPassageway() {
//        RespData<List<PassagewayRtpInfoVO>> respData = new RespData<>();
//        try {
//            int mesLength = headLength * 4; //N个元素，每个元素4个字节
//            long[] dataArray = new long[headLength];
//            setHead(dataArray, codeForGettingRtpInfo, mesLength);
//            byte[] contentBytes = convertLongsToBytes(mesLength, dataArray);
//            Object object = sendRequestToCToGetRtpInfo(contentBytes, false, codeForGettingRtpInfo, null);
//            if (object instanceof Boolean) {
//                //得到boolean值，一定是C返回失败的信息
//                throw new RuntimeException("获取通道RTP流数据失败");
//            } else if (object instanceof Map) {
//                Map<Integer, Object> map = (Map<Integer, Object>) object;
//                List<PassagewayRtpInfoVO> voList = parseBytesForRtp(map);
//                respData.setRespCode(RespCodeEnum.SUCCESS);
//                respData.setData(voList);
//            }
//        } catch(Exception e) {
//            log.error(e.getMessage(), e);
//            respData.setRespCode(RespCodeEnum.FAIL);
//            respData.setReason(e.getMessage());
//        }
//        return respData;
//    }
//
//
//    private List<PassagewayRtpInfoVO> parseBytesForRtp(Map<Integer, Object> map) {
//        Integer msgLength = (Integer) map.get(1);
//        byte[] bytes = (byte[]) map.get(2);
//        List<PassagewayRtpInfoVO> resultList = new ArrayList<>();
//        int headLength = ExecCommendImpl.headLength * 4;
//        int eachBodyLength = 80;
//        int bodyBytesLength = msgLength - headLength;
//        //没有消息体，返回空list
//        if (bodyBytesLength == 0) {
//            return CollectionUtil.getEmptyList();
//        }
//        //消息长度= 20 + n * 80，下面是求出n的值,n就是最终对象的数量
//        int n = (msgLength - headLength) / eachBodyLength;
//        //bytes，前面的20个元素，是头部信息，排除掉
//        byte[] bodyBytes = new byte[bodyBytesLength];
//        int bodyIndex = 0;
//        for (int i = 0; i < msgLength; i++) {
//            //从第21个元素开始，下标20,那么这里截止下标19
//            if (i <= headLength - 1) {
//                continue;
//            }
//            bodyBytes[bodyIndex] = bytes[i];
//            bodyIndex++;
//        }
//        /**
//         * 1）源IP：整形4字节；
//         * 2）源端口：整形4字节
//         * 3）目的IP：整形4字节
//         * 4）目的端口：整形4字节
//         * 5）协议类型：整形4字节 0000:UDP 0001:TCP
//         * 6）流量统计：长整形8字节
//         * 7）起始时间：字符20字节，ex：2020-06-09 15:46:20
//         * 8）结束时间：字符20字节，ex：2020-06-09 15:46:20
//         * 9）视频流信息：12字节
//         *
//         * bodyBytes[],x个元素，每80个元素，构成1个对象
//         *
//         * 0-79,
//         * 80-159,
//         * 160-139,
//         * ....
//         * 每80个元素，可以提取出9个字段，每个字段都有其规定的字节长度，且，不同的字段，类型也不同，bytes需要做不同的转换
//         */
//        //一共n个对象,遍历n次
//        int totalIndex = 0;
//        for (int i = 0; i < n; i++) {
//            //每个对象80个字节
//            byte[] bytesForEachObj = new byte[eachBodyLength];
//            for (int x = 0; x < eachBodyLength; x++) {
//                bytesForEachObj[x] = bodyBytes[totalIndex];
//                totalIndex++;
//            }
//            PassagewayRtpInfoVO vo = new PassagewayRtpInfoVO();
//            //下面就是解析出9个字段
//            //源IP：整形4字节；
//            int length = 4;
//            int startIndex = 0;
//            byte[] temp = getSubBytes(bytesForEachObj, length, startIndex);
//            String sourceIp = getIpFromBytes(temp);
//            vo.setSourceIp(sourceIp);
//            //源端口：整形4字节
//            startIndex = 4;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            int sourcePort = getPortFromBytes(temp);
//            vo.setSourcePort(sourcePort);
//            //目的IP：整形4字节
//            startIndex = 8;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            String targetIp = getIpFromBytes(temp);
//            vo.setTargetIp(targetIp);
//            //目的端口：整形4字节
//            startIndex = 12;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            int targetPort = getPortFromBytes(temp);
//            vo.setTargetPort(targetPort);
//            //协议类型：整形4字节 0000:UDP 0001:TCP 这个不需要转换为int了，直接判断byte数组的最后元素是否为1
//            byte b = bytesForEachObj[19];
//            byte one = 1;
//            String protocolType = Constants.UDP;
//            if (b == one) {
//                protocolType = Constants.TCP;
//            }
//            vo.setProtocolType(protocolType);
//            //流量统计：长整形8字节
//            length = 8;
//            startIndex = 20;
//            // 这里截止到下标为27的元素
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            long flow = bytesToLong(temp, 0, true);
//            vo.setFlow(flow);
//            //起始时间：字符20字节，ex：2020-06-09 15:46:20
//            length = 20;
//            startIndex = 28;
//            //这里截止到下标为47的元素
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            String startTime = getDateStrFromBytes(temp);
//            vo.setStartTime(startTime);
//            //结束时间：字符20字节，ex：2020-06-09 15:46:20
//            startIndex = 48;
//            //这里截止到下标为67的元素
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            String endTime = getDateStrFromBytes(temp);
//            vo.setEndTime(endTime);
//            //1）视频流信息：12字节 不解析
//            length = 12;
//            startIndex = 68;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            vo.setVideoFlow(temp);
//            resultList.add(vo);
//        }
//        return resultList;
//    }
//
//    /**
//     * C返回的byte数组是这样的
//     * byte[] bytes = {
//     *                 5, 0, 10, 66, 100, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0,
//     *                 0x5f,0x34,0x14, (byte) 0xac,0x5c, (byte) 0xf4,0x00,0x00,0x5f,0x34,0x14, (byte) 0xac,0x51, (byte) 0xc3,0x00,0x00,0x00,0x00,0x00,0x00, (byte) 0xa0, (byte) 0x86,
//     *                 0x01,0x00,0x00,0x00,0x00,0x00,0x32,0x30,0x32,0x30,0x2d,0x30,0x36,0x2d,0x32,0x34,0x20,0x31,0x31,0x3a,0x30,0x34,0x3a,0x31,0x36,0x00,0x32,0x30,0x32,0x30,0x2d,
//     *                 0x30,0x36,0x2d,0x32,0x34,0x20,0x31,0x31,0x3a,0x30,0x34,0x3a,0x31,0x37,0x00,0x2a,0x53,0x41,0x49,0x4c,0x49,0x4e,0x47,0x2d,0x2d,0x2d,0x31
//     *         };
//     * 其实C程序返回的数组，每个元素都是十六进制的
//     * 前面20位是日志输出的，已经自动转为十进制了
//     * 20位之后的数据，0x是我手动加的
//     * C那边，一个字节最大支持255，但是java的byte,最大值 127，
//     * 例如 0xac,十进制是172，已经超过127的，那么，就需要把这个值进行转换
//     * 0x5f,0x34,0x14, (byte) 0xac，这4个字节，目前可以构成一个字段，就是ip地址
//     * 调用方法bytesToIntBig()，将这4个字节，转换为一个int值
//     * 再把这个int值，转成ip
//     */
//    private String getIpFromBytes(byte[] bytes) {
//        int value = bytesToIntBig(bytes, 0);
//        String ip = longToIp(value);
//        return ip;
//    }
//
//    //大端
//    public static int bytesToIntBig(byte[] src, int offset) {
//        int value;
//        value = (int) (((src[offset] & 0xFF) << 24)
//                | ((src[offset + 1] & 0xFF) << 16)
//                | ((src[offset + 2] & 0xFF) << 8)
//                | (src[offset + 3] & 0xFF));
//        return value;
//    }
//
//    public static final String longToIp(long number) {
//        String ip = "";
//        for (int i = 3; i >= 0; i--) {
//            ip += String.valueOf((number & 0xff));
//            if (i != 0) {
//                ip += ".";
//            }
//            number = number >> 8;
//        }
//        return ip;
//    }
//
//    private int getPortFromBytes(byte[] bytes) {
//        return TCPSend.byte2int(bytes, 4);
//    }
//
//    private String getDateStrFromBytes(byte[] bytes) {
//        //数组中的元素，都是16进制的byte,转成10进制后，是ASCII码，string构造器可以直接把数组转成string
//        String date = new String(bytes);
//        int length = date.length();
//        if (length >= 20) {
//            // 2020-06-24 11:04:16 返回的时间数据，发现最后有1位乱码的符号，其实有效日期就是19位，因此截取前面的19位,截取 0-18
//            date = date.substring(0, 19);
//        }
//        return date;
//    }
//
//    public static long bytesToLong(byte[] bytes, int offset, boolean small) {
//        long value=0;
//        // 循环读取每个字节通过移位运算完成long的8个字节拼装
//        for(int count=0; count < 8; ++count){
//            int shift=(small ? count: (7-count))<<3;
//            value |=((long)0xff<< shift) & ((long)bytes[offset+count] << shift);
//        }
//        return value;
//    }
//
//    private List<PassagewayRtpVideoFlowInfoVO> parseBytesForRtpVodeoFlow(Map<Integer, Object> map) {
//        Integer msgLength = (Integer) map.get(1);
//        byte[] bytes = (byte[]) map.get(2);
//        List<PassagewayRtpVideoFlowInfoVO> resultList = new ArrayList<>();
//        int headLength = ExecCommendImpl.headLength * 4;
//        int eachBodyLength = 32;
//        int bodyBytesLength = msgLength - headLength;
//        //没有消息体，返回空list
//        if (bodyBytesLength == 0) {
//            return CollectionUtil.getEmptyList();
//        }
//        //消息长度= 20 + n * 32，下面是求出n的值,n就是最终对象的数量
//        int n = (msgLength - headLength) / eachBodyLength;
//        //bytes，前面的20个元素，是头部信息，排除掉
//        byte[] bodyBytes = new byte[bodyBytesLength];
//        int bodyIndex = 0;
//        for (int i = 0; i < msgLength; i++) {
//            //从第21个元素开始，下标20,那么这里截止下标19
//            if (i <= headLength - 1) {
//                continue;
//            }
//            bodyBytes[bodyIndex] = bytes[i];
//            bodyIndex++;
//        }
//        /**
//         * 1）源IP：整形4字节；
//         * 2）源端口：整形4字节
//         * 3）目的IP：整形4字节
//         * 4）目的端口：整形4字节
//         * 5）协议类型：整形4字节
//         * 6）媒体流封装格式：整形4字节，枚举值待定
//         * 7）音频流解码格式：整形4字节，枚举值待定
//         * 8）视频流解码格式：整形4字节，枚举值待定
//         *
//         * bodyBytes[],x个元素，每32个元素，构成1个对象
//         */
//        //一共n个对象,遍历n次
//        int totalIndex = 0;
//        for (int i = 0; i < n; i++) {
//            //每个对象32个字节
//            byte[] bytesForEachObj = new byte[eachBodyLength];
//            for (int x = 0; x < eachBodyLength; x++) {
//                bytesForEachObj[x] = bodyBytes[totalIndex];
//                totalIndex++;
//            }
//            PassagewayRtpVideoFlowInfoVO vo = new PassagewayRtpVideoFlowInfoVO();
//            //下面就是解析出8个字段
//            //源IP：整形4字节；
//            int length = 4;
//            int startIndex = 0;
//            byte[] temp = getSubBytes(bytesForEachObj, length, startIndex);
//            String sourceIp = getIpFromBytes(temp);
//            vo.setSourceIp(sourceIp);
//            //源端口：整形4字节
//            startIndex = 4;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            int sourcePort = getPortFromBytes(temp);
//            vo.setSourcePort(sourcePort);
//            //目的IP：整形4字节
//            startIndex = 8;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            String targetIp = getIpFromBytes(temp);
//            vo.setTargetIp(targetIp);
//            //目的端口：整形4字节
//            startIndex = 12;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            int targetPort = getPortFromBytes(temp);
//            vo.setTargetPort(targetPort);
//            //协议类型：整形4字节 0000:UDP 0001:TCP 这个不需要转换为int了，直接判断byte数组的最后元素是否为1
//            byte b = bytesForEachObj[19];
//            byte one = 1;
//            String protocolType = Constants.UDP;
//            if (b == one) {
//                protocolType = Constants.TCP;
//            }
//            vo.setProtocolType(protocolType);
//            // 媒体流封装格式：整形4字节，枚举值待定
//            startIndex = 20;
//            // 这里截止到下标为23的元素
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            int value = TCPSend.byte2int(temp, 4);
//            vo.setFormatOfMedia(value);
//            // 音频流解码格式：整形4字节，枚举值待定
//            startIndex = 24;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            value = TCPSend.byte2int(temp, 4);
//            vo.setFormatOfDecodeForAudio(value);
//            // 视频流解码格式：整形4字节，枚举值待定
//            startIndex = 28;
//            temp = getSubBytes(bytesForEachObj, length, startIndex);
//            value = TCPSend.byte2int(temp, 4);
//            vo.setFormatOfDecodeForVideo(value);
//            resultList.add(vo);
//        }
//        return resultList;
//    }
//
//    private byte[] getSubBytes(byte[] bytesForEachObj, int length, int startIndex) {
//        byte[] resultBytes = new byte[length];
//        for (int x = 0; x < length; x++) {
//            resultBytes[x] = bytesForEachObj[startIndex];
//            startIndex++;
//        }
//        return resultBytes;
//    }
//
//    @Override
//    public RespData<List<PassagewayRtpVideoFlowInfoVO>> getRtpVideoFlowInfo(PassagewayRtpVideoFlowArgDto dto) {
//        RespData<List<PassagewayRtpVideoFlowInfoVO>> respData = new RespData<>();
//        try {
//            List<ElementsOfDto> list = dto.getList();
//            if (CollectionUtil.ifCollectionIsEmpty(list)) {
//                ElementsOfDto element = new ElementsOfDto();
//                list = new ArrayList<>();
//                list.add(element);
//                dto.setList(list);
//            }
//            byte[] contentBytes = getRequestBytesForVideoFlow(dto);
//            list = dto.getList();
//            StringBuffer buffer = new StringBuffer();
//            // 如果
//            if (ifCollectionIsNotEmpty(list)) {
//                for (ElementsOfDto each: list) {
//                    String json = JSON.toJSONString(each);
//                    buffer.append(json);
//                }
//            }
//            Object object = sendRequestToCToGetRtpInfo(contentBytes, false, codeForGettingRtpVideoFlow, buffer.toString());
//            if (object instanceof Boolean) {
//                //得到boolean值，一定是C返回失败的信息
//                throw new RuntimeException("获取通道RTP视频流数据失败");
//            } else if (object instanceof Map) {
//                Map<Integer, Object> map = (Map<Integer, Object>) object;
//                List<PassagewayRtpVideoFlowInfoVO> voList = parseBytesForRtpVodeoFlow(map);
//                respData.setRespCode(RespCodeEnum.SUCCESS);
//                respData.setData(voList);
//            }
//        } catch(Exception e) {
//            log.error(e.getMessage(), e);
//            respData.setRespCode(RespCodeEnum.FAIL);
//            respData.setReason(e.getMessage());
//        }
//        return respData;
//    }
//
//
//
//    private static byte[] getRequestBytesForVideoFlow(PassagewayRtpVideoFlowArgDto dto) {
//        //消息体，每个对象的长度
//        int eachBodyLength = 3;
//        List<ElementsOfDto> list = dto.getList();
//        int bodySize = list.size();
//        int dataLength = headLength + (bodySize * eachBodyLength); //数据数组长度
//        int mesLength = dataLength * 4;  //消息总长度
//        long[] dataArray = new long[dataLength];
//        dataArray = setHead(dataArray, codeForGettingRtpVideoFlow, mesLength);
//        int index = headLength;
//        long defaultValue = 0xFFFFFFFF;
//        for (int i = 0; i < bodySize; i++) {
//            ElementsOfDto each = list.get(i);
//            String ip = each.getDstIP();
//            if (isBlank(ip)) {
//                dataArray[index] = defaultValue;
//            } else {
//                dataArray[index] = ipToLong(ip);
//            }
//            String port = each.getDstPort();
//            if (isBlank(port)) {
//                dataArray[index+1] = defaultValue;
//            } else {
//                dataArray[index+1] = Long.valueOf(port);
//            }
//            String protocol = each.getProtocol();
//            if (isBlank(protocol)) {
//                // 默认UDP
//                dataArray[index+2] = 0;
//            } else {
//                dataArray[index+2] = Long.valueOf(protocol);
//            }
//            index += eachBodyLength;
//        }
//        byte[] content = convertLongsToBytes(mesLength, dataArray);
//        return content;
//    }
//
//
//    public RespData<Boolean> sggAppPassageway(Passageway passageway) {
//        log.info("sggAppPassageway,way obj:" + JSON.toJSONString(passageway));
//        RespData<Boolean> respData = new RespData<>();
//        try {
//            setType(passageway);
//            if (Constants.UDP.equals(passageway.getType())) {
//                // damon UDP类型的通道，针对传参，进行参数合法性校验
//                boolean argError = false;
//                StringBuffer buffer = new StringBuffer();
//                int shouldStat = passageway.getShouldStat();
//                if (shouldStat < 0 || shouldStat > 3) {
//                    argError = true;
//                    buffer.append("传参shouldStat不合法;");
//                }
//                int shouldFrame = passageway.getShouldFrame();
//                if (!(shouldFrame == 0 || shouldFrame == 1)) {
//                    argError = true;
//                    buffer.append("传参shouldFrame不合法;");
//                }
//                if (shouldFrame == 1) {
//                    // 如果开启插白帧，判断插白帧间隔，长度
//                    int interval = passageway.getIntervalOfFrame();
//                    if (interval < 1 || interval > 255) {
//                        argError = true;
//                        buffer.append("传参intervalOfFrame不合法;");
//                    }
//                    int length = passageway.getLengthOfFrame();
//                    if (length < 1 || length > 255) {
//                        argError = true;
//                        buffer.append("传参lengthOfFrame不合法;");
//                    }
//                }
//                if (argError) {
//                    respData.setRespCode(RespCodeEnum.EXCEPTION);
//                    respData.setReason("CB参数校验不通过：" + buffer.toString());
//                    log.info("CB参数校验不通过:" + buffer.toString());
//                    return respData;
//                }
//            }
//            long startOrStop = codeForStart;
//            if (STATE_STOP.equals(passageway.getState())) {
//                startOrStop = codeForStop;
//            }
//            boolean isTcp = true;
//            String protocolType = passageway.getType();
//            if (!Constants.TCP.equals(protocolType)) {
//                isTcp = false;
//            }
//            byte[] contentBytes = getContentBytes(passageway);
//            /**
//             * 当发送了启动的指令，然后C程序返回失败，那么，可能出现这种情况，
//             *  需要启动50个端口，C程序启动了45个，然后继续启动就失败了，此时C返回结果是失败，
//             *  CB这边没必要去判断是否启动了部分通道， 统一发送一条停止的指令到C
//             *
//             * 启动通道成功，返回true
//             * 启动通道失败，返回false
//             *
//             * 关闭通道成功，返回true
//             * 关闭通道失败，返回false
//             *
//             * 启动成功，需要针对iptables插入规则
//             * 启动失败，那么再发送一条关闭通道的指令，此时，没有必要针对iptalbes做任何操作
//             *
//             * 关闭成功，需要针对iptables删除规则
//             * 关闭失败，没必要针对iptables做操作
//             */
//            /**
//             * damon 2020-09-22
//             * 如果C程序返回错误码是211的时候，就不用再发送停止通道的指令了；
//             * 逻辑如下：
//             * 1.单个端口启动的时候，如果错误码是211，则认为本次启动是成功的
//             * 2.单个端口启动返回非211的错误码，或者批量启动返回任何错误，按照原有逻辑停止通道再启动通道
//             */
//            PassagewayParam param = passageway.getPassagewayParams().get(0);
//            String monitorPort = param.getMonitorPort();
//            String[] ports = monitorPort.split(":");
//            boolean isSinglePort = ports.length == 1;
//            // C程序返回状态码为0，那么就一定是成功
//            int successCode = 0;
//            int resCode = sendTcpRequestToInvokeTheC(contentBytes, isTcp, startOrStop, passageway);
//            boolean opResult = false;
//            if (isSinglePort) {
//                // 单个端口
//                if (successCode == resCode) {
//                    opResult = true;
//                } else if (211 == resCode){
//                    // 单个端口，C返回211，对于王思旺那边，是成功的，说明在启动之前，通道已经存在
//                    // 因为青柠平台的通道，不是在网闸页面去启动和停止的，所以会造成这种特殊情况
//                    opResult = true;
//                    // 对于web这边，本次操作是失败的，需要定义reason，用于前端展示
//                    respData.setCode(resCode);
//                    respData.setReason("请勿重复启动通道");
//                } else {
//                    opResult = false;
//                }
//            } else {
//                // 端口段 只有底层返回成功的状态码，才是成功
//                if (successCode == resCode) {
//                    opResult = true;
//                } else {
//                    opResult = false;
//                    // 操作失败，再判断本次是否为启动操作，如果是，那么还需要发送停止的命令，这是和单个端口的区别
//                    if (startOrStop == codeForStart) {
//                        log.error("start passageway failed, about to send one more request to stop passageway");
//                        passageway.setState(Constants.STATE_STOP);
//                        contentBytes = getContentBytes(passageway);
//                        //这边去执行关闭指令，那么有可能关闭失败
//                        resCode = sendTcpRequestToInvokeTheC(contentBytes, isTcp, codeForStop, passageway);
//                        if (successCode == resCode) {
//                            // 说明关闭通道成功，但是用户的目的是启动，此时抛出异常提示用户
//                            log.error("start通道失败，其后的stop通道成功，本次启动通道操作失败");
//                            throw new RuntimeException("启动通道失败");
//                        } else {
//                            // 这是关闭通道也失败了，那么无法启动，也无法关闭，说明C程序目前已经失去处理能力
//                            log.error("start通道失败，其后的stop通道也失败，本次启动通道操作失败");
//                            throw new RuntimeException("启动通道的服务存在异常，请联系管理员");
//                        }
//                    }
//                }
//            }
//            // 上面，有可能会给code赋值，因此这里做null判断
//            if (respData.getCode() == null) {
//                respData.setRespCode(RespCodeEnum.SUCCESS);
//            }
//            respData.setData(opResult);
//            return respData;
//        } catch (Exception e) {
//            respData.setRespCode(RespCodeEnum.EXCEPTION);
//            respData.setReason("CB内部处理异常：" + e.getMessage());
//            log.error("ExecCommendImpl.appendGOTCPRules异常：", e);
//        }
//        return respData;
//    }
//
//
//    private void setType(Passageway passageway) {
//        switch (passageway.getType()) {
//            case Constants.RTP:
//            case Constants.SIP:
//                passageway.setType(Constants.UDP);
//                break;
//            case Constants.HTTP:
//                passageway.setType(Constants.TCP);
//                break;
//        }
//    }
//
//    private byte[] getContentBytes(Passageway passageway) throws Exception {
//        long startOrStop = codeForStart;
//        if(Constants.STATE_STOP.equalsIgnoreCase(passageway.getState())){
//            startOrStop = codeForStop;
//        }
//        byte[] contentBytes = null;
//        //目前通道管理列表页的每个对象，起通道数据，其实都只有1条，虽然现在是用了list接收通道数据，但其实只有1条数据，因此，get(0)获取第1且唯一的通道
//        List<PassagewayParam> pps = passageway.getPassagewayParams();
//        PassagewayParam way = pps.get(0);
//        //String nodeIp = applicationProperties.nodeIp;
//        int[] listenPorts = changePortToIntArray(way.getMonitorPort());
//        //0 client, 1 server VCS项目固定为client
//        int clientOrServer = 0;
//        //0 UDP协议 1 TCP协议
//        int udpOrTcp = 1;
//        String protocolType = passageway.getType();
//        if (Constants.TCP.equals(protocolType)) {
//            udpOrTcp = 1;
//        } else if (Constants.UDP.equals(protocolType)) {
//            udpOrTcp = 0;
//        }
//        contentBytes = tcpSend(startOrStop, clientOrServer, way.getMonitorIp(), listenPorts, udpOrTcp, way.getTargetIp(), listenPorts, udpOrTcp, passageway);
//        return contentBytes;
//    }
//
//    /**
//     * 发送TCP请求，调用C程序，去启动进程
//     * 这个CB程序，和C程序，其实是部署在同一台机器上的，因此，发送TCP请求的地址为本地
//     * 有个坑需要注意，尤其是开发环境，如果tcp和UDP的json文件所配置的IP地址，是服务器的ip地址，而不是 127.0.0.1，那么，下面的 tcpIp也要写IP地址
//     * 然后是端口，TCP类型的通道，需要调用TCP进程的C程序，UDP类型的通道，调用UDP进程的C程序，二者的端口是不一样的
//     * @param contentBytes
//     */
//    private int sendTcpRequestToInvokeTheC(byte[] contentBytes, boolean isTcpType, long actionCode, Passageway passageway) {
//        String tcpIp = "127.0.0.1";
//        int port = getPort(isTcpType);
//        String action = getAction(actionCode);
//        log.info(String.format("CB is about to send request to C, action=%s, passageway name=%s", action, passageway.getName()));
//        TCPSend tcpSend = new TCPSend(tcpIp, contentBytes, port);
//        return tcpSend.send(action);
//    }
//
//    private Object sendRequestToCToGetRtpInfo(byte[] contentBytes, boolean isTcpType, long actionCode, String logInfo) {
//        String ip = "127.0.0.1";
//        int port = getPort(isTcpType);
//        String action = getAction(actionCode);
//        if (logInfo != null) {
//            log.info(String.format("CB is about to send request to C for getting RTP info, action=%s, args=[%s]", action, logInfo));
//        } else {
//            log.info(String.format("CB is about to send request to C for getting RTP info, action=%s, no arg", action));
//        }
//        TCPSend tcpSend = new TCPSend(ip, contentBytes, port);
//        return tcpSend.getRtpInfo(action);
//    }
//
//    private int getPort(boolean isTcpType) {
//        int port = 4298;
//        if (!isTcpType) {
//            //如果不是tcp类型的通道，那么需要调用UDP进程的C程序服务
//            port = 4297;
//        }
//        return port;
//    }
//
//    private String getAction(long actionCode) {
//        String action = "";
//        if (actionCode == codeForStart) {
//            action = "start";
//        } else if (actionCode == codeForStop) {
//            action = "stop";
//        } else if (actionCode == codeForGettingRtpInfo) {
//            action = "getRtpInfo";
//        } else if (actionCode == codeForGettingRtpVideoFlow) {
//            action = "getRtpVideoFlow";
//        }
//        return action;
//    }
//
//
//    /**
//     * 注意，这是把端口段 50001:50009，封装为端口数组[50001, 50009]，数组只有2个元素
//     */
//    public static int[] changePortToIntArray(String port) {
//        String[] ports = port.split(":");
//        List<Integer> temp = new ArrayList<>();
//        for (String p: ports) {
//            if (isBlank(p)) {
//                continue;
//            }
//            temp.add(Integer.valueOf(p));
//        }
//        int[] listenPorts = new int[temp.size()];
//        for (int i = 0; i < temp.size(); i++) {
//            listenPorts[i] = temp.get(i);
//        }
//        return listenPorts;
//    }
//
//    // 本地测试
///*    public void localTest() {
//        try {
//            String listenIp = "172.20.54.75";
//            String port = "17000:17005";
//            Passageway passageway = new Passageway();
//            passageway.setName("damon本地测试通道");
//            passageway.setType(UDP);
//            passageway.setPortType(true);
//            passageway.setShouldStat(0);
//            passageway.setShouldFrame(0);
//            passageway.setLengthOfFrame(0);
//            passageway.setIntervalOfFrame(0);
//            PassagewayParam param = new PassagewayParam();
//            param.setMonitorIp(listenIp);
//            param.setLinkIp(listenIp);
//            param.setMonitorPort(port);
//            param.setTargetIp("172.20.52.105");
//            param.setTargetPort(port);
//            List<PassagewayParam> list = new ArrayList<>();
//            list.add(param);
//            passageway.setPassagewayParams(list);
//            RespData<Boolean> respData = stopPassageway(passageway);
//            log.info("结果=" + respData);
//        } catch (Exception e) {
//            log.error("本地测试异常,msg:" + e.getMessage(), e);
//        }
//    }*/
//
//    private static long[] setHead(long[] dataArray, long msgType, int mesLength) {
//        dataArray[0] = msgType;/* 消息类型ID */
//        dataArray[1] = mesLength;/* 消息总长度，包括消息头部长度 */
//        dataArray[2] = 1; /* 针对主动发送的消息是否需要对端回response响应报文，如果需要则填写1，对端发送消息时msgType=本消息发出的消息msgType+1 */
//        dataArray[3] = 100; /* 消息体版本号，扩展使用，当前固定填写100 */
//        dataArray[4] = 0;  /* 错误码，成功填写0 */
//        dataArray[5] = 0; // 保留字段，暂时不使用，但是位置需要保留
//        return dataArray;
//    }
//
//    // 支持通道差异化的版本，在启动通道的时候，还需要设置额外的消息头
//    private static long[] setExtraHeadForStartPassageway(long[] dataArray, int location, Passageway passageway) {
//        int start = headLength;
//        dataArray[start] = 0; /* 位置 0：client，1：server 这里固定赋值为0*/
//        dataArray[start+1] = passageway.getUdpSendInterval();
//        dataArray[start+2] = 0; // heartbeat赋值为0
//        dataArray[start+3] = passageway.getTcpBuffMaxCount() == null ? 128 : passageway.getTcpBuffMaxCount();
//        dataArray[start+4] = passageway.getUdpBuffMaxCount() == null ? 128 : passageway.getUdpBuffMaxCount();
//        dataArray[start+5] = passageway.getSggMultiLines() == null ? 5 : passageway.getSggMultiLines();
//        /**
//         * 解释一波
//         * start+6这个字段不能再使用了
//         * 因为不管是针网闸还是光闸，C程序的代码都只有一套，
//         * 那么光闸那边，已经使用了 start+6的字段，所以，网闸这边就不能用了，现在就只能用 start+7这个字段了
//         */
//        dataArray[start+6] = 0L;
//        dataArray[start+7] = passageway.getTimeOut();  // 通道的超时时间，默认是0
//        return dataArray;
//    }
//
//    //这个是支持通道差异化的版本
//    private static byte[] tcpSend(long msgType,int location,String listenIp,int[] listenPorts,int listenType,String remoteIp,
//                                  int[] remotePorts,int remoteType, Passageway passageway) throws Exception {
//
//        int minListenPort = listenPorts[0]; //最小监听端口
//        int maxListenPort;
//        if (listenPorts.length > 1) {
//            maxListenPort = listenPorts[1]; //最大监听端口
//        } else {
//            maxListenPort = listenPorts[0];
//        }
//        int minRemotePort = remotePorts[0]; //最大目标端口
//        int portNum = maxListenPort-minListenPort+1; // 端口数量
//        // n + (portNum * x) n是for循环上面的元素个数，x是for 循环中，元素个数
//        //头部信息长度
//        int headLength;
//        boolean isStart = Constants.STATE_START.equals(passageway.getState());
//        // 停止通道的时候，不需要setExtraHeadForStartPassageway
//        if (isStart) {
//            headLength = ExecCommendImpl.headLength + 8; // 目前长度14
//        } else {
//            headLength = ExecCommendImpl.headLength;
//        }
//        //消息体，每个对象的长度
//        int eachBodyLength = 8;
////        boolean isListenIpIpv6 = IPAddressUtil.isIPv6LiteralAddress(listenIp);
////        boolean isRemoteIpIpv6 = IPAddressUtil.isIPv6LiteralAddress(remoteIp);
//        int dataLength = headLength + (portNum * eachBodyLength); //数据数组长度
//        int mesLength = dataLength * 4;  //消息总长度
//        long[] dataArray = new long[dataLength];
//        long rtpParam = getRtpParam(passageway);
//        dataArray = setHead(dataArray, msgType, mesLength);
//        if (isStart) {
//            dataArray = setExtraHeadForStartPassageway(dataArray, location, passageway);
//        }
//        int index = headLength;
//        for (int port = minListenPort; port <= maxListenPort ; port++) {
//            int tracking = index;
////            dataArray[tracking] = isListenIpIpv6 ? ipv6 : ipv4;
////            if (isListenIpIpv6) {
////                long[] array = convertIpv6ToLongArray(listenIp);
////                dataArray[++tracking] = array[0];
////                dataArray[++tracking] = array[1];
////                dataArray[++tracking] = array[2];
////                dataArray[++tracking] = array[3];
////            } else {
//            dataArray[tracking] = ipToLong(listenIp); //监听IP地址
////                dataArray[++tracking] = 0;
////                dataArray[++tracking] = 0;
////                dataArray[++tracking] = 0;
////            }
//            dataArray[++tracking] = port; //监听端口
//            dataArray[++tracking] = listenType;  //协议 0 UDP协议 1 TCP协议
////            dataArray[++tracking] = isRemoteIpIpv6 ? ipv6 :ipv4;
////            if (isRemoteIpIpv6){
////                long[] array = convertIpv6ToLongArray(remoteIp); //目标地址
////                dataArray[++tracking] = array[0];
////                dataArray[++tracking] = array[1];
////                dataArray[++tracking] = array[2];
////                dataArray[++tracking] = array[3];
////            } else {
//            dataArray[++tracking] = ipToLong(remoteIp); //目标地址
////                dataArray[++tracking] = 0;
////                dataArray[++tracking] = 0;
////                dataArray[++tracking] = 0;
////            }
//            dataArray[++tracking] = minRemotePort; //端口号5555
//            dataArray[++tracking] = remoteType;  //协议 0 UDP协议 1 TCP协议
//            dataArray[++tracking] = rtpParam;
//            dataArray[++tracking] = 0;//加密方式，视频流不加密
//            minRemotePort += 1;
//            index += eachBodyLength;
//        }
////        log.info("dataArray is: " + Arrays.toString(dataArray));
//        byte[] content = convertLongsToBytes(mesLength, dataArray);
//        return content;
//    }
//
//    /**
//     * 4个字段，用于接收4个字节的参数，
//     * 每个字段都是int值，例如 1 1 100 255
//     * 那么从高位到低位就是 255 100 1 1
//     * 将每个数字转成十六进制的字符串，255 => FF, 100 => 64, 1 => 01, 1 => 01
//     * 进行拼接 FF640101
//     * 其实 FF640101就可以了，但问题是，此时的FF640101是字符串，我传给C的值，是数字，所以将该字符串转成数字
//     * @param passageway
//     * @return
//     */
//    private static long getRtpParam(Passageway passageway) {
//        long rtpParam = passageway.getRtpParam();
//        if (rtpParam != 0) {
//            //为了灵活点，支持rtpParam直接传参，这样就不去读取另外的4个字段了
//            return rtpParam;
//        }
//        String prefix = "0";
//        //int转为十六进制，intToHex，如果大于15，那么会返回两个字符
//        String first = MathUtil.intToHex(passageway.getLengthOfFrame());
//        // 小于等于15，那么只返回一个数字，那么需要在前面拼接一个0
//        if (first.length() == 1) {
//            first = prefix + first;
//        }
//        String second = MathUtil.intToHex(passageway.getIntervalOfFrame());
//        if (second.length() == 1) {
//            second = prefix + second;
//        }
//        String third = MathUtil.intToHex(passageway.getShouldFrame());
//        //第3,4位，要求的效果是 01,00
//        third = prefix + third;
//        String fourth = MathUtil.intToHex(passageway.getShouldStat());
//        fourth = prefix + fourth;
//        String append = first + second + third + fourth;
//        log.info("append result for rtp param=" + append);
//        rtpParam = MathUtil.hexToDecimal(append);
//        if (rtpParam == 0) {
//            rtpParam = 0X00000000;
//        }
//        log.info("rtp param=" + rtpParam);
//        return rtpParam;
//    }
//
//    private static byte[] convertLongsToBytes(int mesLength, long[] dataArray) {
//        int offset = 0;
//        byte[] content = new byte[mesLength];
//        for (long n : dataArray) {
//            intToBytes(n, content, offset, false);
//            offset = offset + 4;
//        }
//        return content;
//    }
//
//    /**
//     * java 为大端byte
//     * C 为小端byte
//     * @param n
//     * @param array
//     * @param offset
//     * @param big
//     */
//    private static void intToBytes( long n, byte[] array, int offset,boolean big){
//        if(big){
//            array[3+offset] = (byte) (n & 0xff);
//            array[2+offset] = (byte) (n >> 8 & 0xff);
//            array[1+offset] = (byte) (n >> 16 & 0xff);
//            array[offset] = (byte) (n >> 24 & 0xff);
//        }else{
//            array[offset] = (byte) (n & 0xff);
//            array[1+offset] = (byte) (n >> 8 & 0xff);
//            array[2+offset] = (byte) (n >> 16 & 0xff);
//            array[3+offset] = (byte) (n >> 24 & 0xff);
//        }
//    }
//
//    /**
//     * 如果IP是ipv6地址，那么先把ip地址转为byte数组，这里利用开源的工具直接转换
//     * 得到byte[]: [64, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -106]
//     * 然后把它转为若干int值，因为int是4个字节，所以，从byte[]中，依次获取4个元素，然后转为int值
//     * 将int放到一个long[]中
//     */
//    private static long[] convertIpv6ToLongArray(String ip) throws AddressStringException {
//        IPAddressString str = new IPAddressString(ip);
//        IPAddress addr = str.toAddress();
//        byte[] bytes = addr.getBytes();
//        long[] dataArray = new long[4];
//        int intValue  = bytesToInt(bytes, 0);
//        dataArray[0] = intValue;
//        intValue = bytesToInt(bytes, 4);
//        dataArray[1] = intValue;
//        intValue = bytesToInt(bytes, 8);
//        dataArray[2] = intValue;
//        intValue = bytesToInt(bytes, 12);
//        dataArray[3] = intValue;
//        return dataArray;
//    }
//
//    private static long ipToLong(String ip) {
//        String[] arr = ip.split("\\.");
//        long result = 0;
//        for (int i = 0; i <= 3; i++) {
//            long ipl = Long.parseLong(arr[i]);
//            result |= ipl << ((3-i) << 3);
//        }
//        return result;
//    }
//
//    /**
//     * 网上找的
//     * 这个方法和本类中 intToBytes,传参big为false是呼应的
//     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序
//     * @param src byte数组
//     * @param offset 从数组的第offset位开始
//     * @return int数值
//     */
//    private static int bytesToInt(byte[] src, int offset) {
//        int value;
//        value =  ((src[offset] & 0xFF)
//                | ((src[offset+1] & 0xFF)<<8)
//                | ((src[offset+2] & 0xFF)<<16)
//                | ((src[offset+3] & 0xFF)<<24));
//        return value;
//    }
//
//    private void getFilePath(List<String> filePaths, File engineFolder) throws IOException {
//        if (engineFolder.exists() && engineFolder.listFiles()!= null){
//            for (File file : engineFolder.listFiles()) {
//                filePaths.add(file.getCanonicalPath());
//            }
//        }
//    }
//
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
//    public RespData<Boolean> updateTaskProperties(Map<String, HashMap<String, String>> map) {
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
//}
