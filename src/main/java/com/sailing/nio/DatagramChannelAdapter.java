package com.sailing.nio;

import com.sailing.nio.common.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

// 实现了Sender和Receiver
public class DatagramChannelAdapter implements Sender, Receiver, Closeable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicBoolean isSending = new AtomicBoolean();
    private final DatagramChannel channel;
    private final IoProvider ioProvider;
    private final UdpDataDispatcher dispatcher;

    private final Queue<UDPSendSnapshot> queue = new ConcurrentLinkedQueue<>();
    private final ReceiveUdpListener receiverUdpListener;

    DatagramChannelAdapter(DatagramChannel channel, IoProvider ioProvider, ReceiveUdpListener receiverUdpListener) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        this.receiverUdpListener = receiverUdpListener;
        dispatcher = new UdpDataDispatcher(channel);

        // 非阻塞模式下操作
        channel.configureBlocking(false);
    }

    @Override
    public boolean postReceiveAsync() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }

        // 注册能不能输入
        return ioProvider.registerInput(channel, inputCallback);
    }

    @Override
    public void start() {
        try {
            postReceiveAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean postSendAsync() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }

        // 当前发送的数据附加到回调中
        return ioProvider.registerOutput(channel, outputCallback);
    }

    @Override
    public void send(String message, InetSocketAddress remoteAddress) {
        queue.offer(new UDPSendSnapshot(message,remoteAddress));
        requestSend();
    }

    private void requestSend() {
        if (isSending.compareAndSet(false,true) ) {
            if (queue.size() <= 0){
                isSending.set(false);
                return;
            }
            try {
                if (!postSendAsync()) {
                    isSending.set(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
                CloseUtils.close(this);
            }
        }

    }

    @Override
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) {
            // 解除注册回调
            ioProvider.unRegisterInput(channel);
            ioProvider.unRegisterOutput(channel);
            // 关闭
            CloseUtils.close(channel);
        }
    }

    // 输入的数据操作
    private final HandleProviderCallback inputCallback = new HandleProviderCallback() {
        @Override
        protected void onProviderIo() {


            if (isClosed.get()) {
                return;
            }
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!inputCallback");
            ReceiveUdpData receiveUdp = dispatcher.receive();

            try {
                if (receiveUdp == null) {
                    throw new IOException();
                }
                postReceiveAsync();
                receiverUdpListener.onReceiveUdpListener(receiveUdp.getBytes(),receiveUdp.getTotal(),receiveUdp.getAddress(),receiveUdp.getPort());
            } catch (IOException e) {
                CloseUtils.close(DatagramChannelAdapter.this);
            }
        }
    };

    // 输出的数据操作
    private final HandleProviderCallback outputCallback = new HandleProviderCallback() {
        @Override
        protected void onProviderIo() {
            if (isClosed.get() || queue.size() == 0) {
                return;
            }
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!outputCallback");
            synchronized (isSending) {
                UDPSendSnapshot snapshot = queue.poll();
                dispatcher.sendMessage(snapshot.getMessage(),snapshot.getRemoteAddress());
                isSending.set(false);
            }
        }
    };


}