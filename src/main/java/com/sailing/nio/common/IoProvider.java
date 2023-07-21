package com.sailing.nio.common;

import java.io.Closeable;
import java.nio.channels.DatagramChannel;

// 用于Channel向Selector注册
public interface IoProvider extends Closeable {

    boolean registerInput(DatagramChannel channel, HandleProviderCallback callback);

    boolean registerOutput(DatagramChannel channel, HandleProviderCallback callback);

    void unRegisterInput(DatagramChannel channel);

    void unRegisterOutput(DatagramChannel channel);


}
