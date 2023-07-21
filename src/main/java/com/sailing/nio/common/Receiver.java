package com.sailing.nio.common;

import java.io.Closeable;
import java.io.IOException;

// 接收接口
public interface Receiver extends Closeable {

    // 触发异步的接收请求
    boolean postReceiveAsync() throws IOException;

    // 开始监听
    void start();
}