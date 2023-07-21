package com.sailing.tcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 16:46
 */
@Slf4j
@Service
public class GetNextPort {

    private final Map<String,Socket> socketMap = new HashMap<>();

    @Value("${portManager:4011}")
    private int port;

    public int getNextPort(String ip, PortCommendType commend)  {
        try {
            Socket socket = getSocket(ip, port);
            byte[] bytes = new byte[10];
            socket.getOutputStream().write(commend.getValue().getBytes());
            socket.getOutputStream().flush();
            int len = socket.getInputStream().read(bytes);
            return Integer.parseInt(new String(Arrays.copyOf(bytes,len)).substring(1));

        } catch (IOException e) {
            if (e instanceof SocketTimeoutException){
                log.error("当前server压力过大无法承受当前请求,改造服务端");
            }
            log.error("error",e);
        }
        return new Random().nextInt(50000 - 20000 + 1) + 20000;
    }

    private  Socket getSocket(String ip, int port) throws IOException {
        Socket socket = socketMap.get(ip);
        if (socket!= null && socket.isConnected()) return socket;

        socket = new Socket(ip, port);
        socket.setSoTimeout(5000);
        socketMap.put(ip,socket);
        return socket;
    }
}
/*
   定义     CURRENT_TCP_PORT("t"),
            OTHER_TCP_PORT("T"),
            CURRENT_UDP_PORT("u"),
            OTHER_UDP_PORT("U");

   返回  下级udp的端口为XXXXX(端口严格5位数不足五位在前面补0):   u|xxxxx
*/
enum PortCommendType {
    CURRENT_TCP_PORT("t"),
    OTHER_TCP_PORT("T"),
    CURRENT_UDP_PORT("u"),
    OTHER_UDP_PORT("U");

    private final String value;

    PortCommendType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}