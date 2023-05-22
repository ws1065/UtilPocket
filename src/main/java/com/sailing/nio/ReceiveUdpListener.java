package com.sailing.nio;

import java.net.InetSocketAddress;

/**
     * 收到监听UDP消息之后的回调
     */
public     interface ReceiveUdpListener {
        void onReceiveUdpListener(byte[] data, int length, InetSocketAddress address, int port);
    }