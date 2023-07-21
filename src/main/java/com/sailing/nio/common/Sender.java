package com.sailing.nio.common;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

// 发送接口
public interface Sender extends Closeable {

    // 触发异步的发送请求
    boolean postSendAsync() throws IOException;

    void send(String message, InetSocketAddress remoteAddress);
}