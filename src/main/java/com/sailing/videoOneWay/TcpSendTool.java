package com.sailing.videoOneWay;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author damon
 * @date 2021-01-06
 * 从TCPSend类复制过来的，然后做调整
 */
@Slf4j
public class TcpSendTool {

    private String ip;
    private byte[] content;
    private int port;

    //Socket等待响应时间，暂时设置1分钟，单位毫秒
    private int timeout = 60 * 1000;

    public TcpSendTool(String ip, byte[] content, int port){
        this.ip = ip;
        this.content = content;
        this.port = port;
    }

    public int send(String action){
        int resCode = -1;
        Socket socket = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            if(StringUtils.isNotBlank(ip) && ip.contains(":")){
                String[] ipPort = ip.split(":");
                ip = ipPort[0];
                port = Integer.valueOf(ipPort[1]);
            }
            log.info("TCPSend,send() action=" + action + "，即将发送消息目标地址----->"+ip+"端口--->"+port);
            // 建立连接
            socket = new Socket(ip,port);
            socket.setSoTimeout(timeout);
            // 发送数据
            os = socket.getOutputStream();
            os.write(content);
            os.flush();
            log.info(String.format("TCPSend send() 发送消息成功,action=%s，目标IP:%s,端口:%s", action, ip, port));
            /**
             * 2020-05-28 进行改造
             * 需要获取C程序的响应结果，结果为成功，才返回结果给到sgg后端，后端再返回到前端
             *
             * 下面是C程序返回的结果的结构体，且，C程序不是异步开启/关闭通道线程的，只有操作成功之后，才会返回结果给这边
             * 返回的是整形4字节数组 [420A0002,20,0,100, 0]
             *
             * unsigned int msgType;       消息类型ID
             * unsigned int msgLen;        消息总长度，包括消息头部长度
             * unsigned int ackFlag;       针对主动发送的消息是否需要对端回response响应报文，如果需要则填写1，对端发送消息时msgType=本消息发出的消息msgType+1
             * unsigned int version;        消息体版本号，扩展使用，当前固定填写100
             * unsigned int errCode;       错误码，成功填写0
             * unsigned int reserve: 保留字段
             *
             */
            //关于把inputstream转换成byte[]，网上找的那些方法都不行，在while循环中，就停在那里很久，而且结果也不对，下面这是领导提供的方案
            is = socket.getInputStream();
            byte[] bytes = new byte[24];
            is.read(bytes);
            is.close();
            resCode = checkIfResponseOK(bytes);
            if (resCode == 0) {
                // 返回0，一定是成功
                log.info("C程序响应结果为成功");
            } else {
                // 不是0，不一定是失败，由上层判断成功还是失败
                log.info("C程序响应code为" + resCode);
            }
        } catch (Exception e) {
            log.error("TCPSend发送消息异常："+e.getMessage(), e);
        } finally {
            if(os!=null){
                try {
                    os.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

            if(socket!=null){
                try {
                    if(!socket.isClosed()){
                        socket.shutdownOutput();
                        socket.shutdownInput();
                        socket.close();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return resCode;
    }

    private int checkIfResponseOK(byte[] bytes) {
        // 一共6个字段
        int length = 6;
        int[] results = new int[length];
        for (int i = 0; i < length; i++) {
            byte[] bytes1 = new byte[]{bytes[i*4],bytes[i*4+1],bytes[i*4+2],bytes[i*4+3]};
            int result = byte2int(bytes1,4);
            results[i] = result;
        }

        log.info("消息类型：0x"+Integer.toHexString(results[0]).toUpperCase());
        log.info("消息长度："+results[1]);
        log.info("是否回包："+results[2]);
        log.info("消息版本："+results[3]);
        log.info("消息结果："+results[4]);
        /*
         * damon 2020-09-22
         * 如果C程序返回错误码是211的时候，就不用再发送停止通道的指令了；
         * 逻辑如下：
         * 1.单个端口启动的时候，如果错误码是211，则认为本次启动是成功的
         * 2.单个端口启动返回非211的错误码，或者批量启动返回任何错误，按照原有逻辑停止通道再启动通道
         *
         * 这个作为一个处于底层的方法，不做太多限制，这里只返回响应码，不判断是否成功
         */
        int resCode = results[4];
        return resCode;
    }

    public static int byte2int(byte[] data, int n) {
        switch (n) {
            case 1:
                return (int) data[0];
            case 2:
                return (int) (data[0] & 0xff) | (data[1] << 8 & 0xff00);
            case 3:
                return (int) (data[0] & 0xff) | (data[1] << 8 & 0xff00) | (data[2] << 16 & 0xff0000);
            case 4:
                return (int) (data[0] & 0xff) | (data[1] << 8 & 0xff00) | (data[2] << 16 & 0xff0000)
                        | (data[3] << 24 & 0xff000000);
            default:
                return 0;
        }
    }
}
